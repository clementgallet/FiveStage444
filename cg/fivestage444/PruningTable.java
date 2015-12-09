package cg.fivestage444;

import cg.fivestage444.Coordinates.RawCoordState;
import cg.fivestage444.Coordinates.SymCoordState;

import java.io.*;


public class PruningTable {

	private int n_moves;
	private int n_size;
	private int inv_depth;
	private SymCoordState sym;
	private RawCoordState[] raws;

	private byte[] table;

	private PruningTable(){}

	public PruningTable(SymCoordState sym, RawCoordState raw, int n_moves, int inv_depth){
		this.sym = sym;
		raws = new RawCoordState[1];
		raws[0] = raw;

		this.n_moves = n_moves;
		this.inv_depth = inv_depth;

		n_size = this.sym.sc.N_COORD;
		for (RawCoordState raw1 : raws) {
			n_size *= raw1.rc.N_COORD;
		}
	}

	public PruningTable(SymCoordState sym, RawCoordState raw, RawCoordState raw2, int n_moves, int inv_depth){
		this.sym = sym;
		raws = new RawCoordState[2];
		raws[0] = raw;
		raws[1] = raw2;

		this.n_moves = n_moves;
		this.inv_depth = inv_depth;

		n_size = this.sym.sc.N_COORD;
		for (RawCoordState raw1 : raws) {
			n_size *= raw1.rc.N_COORD;
		}
	}

	private void moveTo( int m, PruningTable p ){
		sym.moveTo( m, p.sym );
		for ( int i = 0; i < raws.length; i++){
			raws[i].moveTo( m, p.raws[i] );
		}
	}

	private void writeTable (int index, int value) {
		table[index >> 1] ^= (0x0f ^ value) << ((index & 1) << 2);
	}

	public int readTable (int index) {
		return (table[index >> 1] >> ((index & 1) << 2)) & 0x0f;
	}

	private int get(){
		int idx = sym.coord;
		for (RawCoordState raw : raws) {
			idx = idx * raw.rc.N_COORD + raw.conjugate(sym.sym);
		}
		return idx;
	}

	private void set( int idx ){
		for ( int i = raws.length - 1; i >= 0; i--){
			raws[i].coord = idx % raws[i].rc.N_COORD;
			idx /= raws[i].rc.N_COORD;
		}
		sym.coord = idx;
		sym.sym = 0;
	}

	private void normalise(){
		for (RawCoordState raw : raws) {
			raw.coord = raw.conjugate(sym.sym);
		}
		sym.sym = 0;
	}

	public void initTable(){
		table = new byte[(n_size+1)/2];
		fillTable();
	}

	public void initTable(File f){
		table = new byte[(n_size+1)/2];
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			in.readFully(table);
		} catch (Exception e) {
			fillTable();
			try {
				DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
				out.write(table);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void fillTable(){
		/* Create a new instance of PruningTable with the same classes in it */
		PruningTable p = new PruningTable();
		try {
			p.sym = sym.getClass().newInstance();
			p.sym.sc = sym.sc;
			p.raws = new RawCoordState[raws.length];
			for ( int i = 0; i < raws.length; i++){
				p.raws[i] = raws[i].getClass().newInstance();
				p.raws[i].rc = raws[i].rc;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IllegalAccessException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		/* Create the pruning table and fill with the solved states */
		for (int i = 0; i < (n_size+1)/2; i++)
			table[i] = -1;

		int done = sym.sc.SolvedStates.length;
		for (RawCoordState raw1 : raws) done *= raw1.rc.solvedStates.length;

		for (int d = 0; d < done; d++){
			int dd = d;
			for (RawCoordState raw : raws) {
				raw.coord = raw.rc.solvedStates[dd % raw.rc.solvedStates.length];
				dd /= raw.rc.solvedStates.length;
			}
			sym.coord = sym.sc.SolvedStates[dd];
			sym.sym = 0;
			writeTable(get(), 0);
		}

		/* Build the table */
		int depth = 0;
		while (( done < n_size ) && ( depth < 15 )) {
			boolean inv = depth > inv_depth;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<n_size; i++) {
				if (readTable(i) != select) continue;
				set(i);
				for (int m=0; m<n_moves; m++) {
					moveTo(m, p);
					int idx = p.get();
					if (readTable(idx) != check) continue;
					done++;
					if (inv) {
						writeTable(i, depth);
						int nsym = 1;
						unique++;
						for (int j=0; j<sym.getSyms().length; j++) {
							long symS = sym.getSyms()[j];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 0x1L) == 0) continue;
								sym.sym = k*sym.getSyms().length + j;
								int sym_idx = get();
								if( sym_idx == i )
									nsym++;
								if (readTable(sym_idx) == 0x0f) {
									writeTable(sym_idx, depth);
									done++;
								}
							}
						}
						pos += 48/nsym; // TODO: find the correct value or drop off
						break;
					} else {
						writeTable(idx, depth);
						int nsym = 1;
						unique++;
						p.normalise();
						for (int j=0; j<p.sym.getSyms().length; j++) {
							long symS = p.sym.getSyms()[j];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 0x1L) == 0) continue;
								p.sym.sym = k*p.sym.getSyms().length + j;
								int sym_idx = p.get();
								if( sym_idx == idx )
									nsym++;
								if (readTable(sym_idx) == 0x0f) {
									writeTable(sym_idx, depth);
									done++;
								}
							}
						}
						pos += 48/nsym; // TODO: find the correct value or drop off
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
