package cg.fivestage444;

import cg.fivestage444.Coordinates.RawCoord;
import cg.fivestage444.Coordinates.SymCoord;

import java.io.*;


public class PruningTable {

	private int n_moves;
	private int n_size;
	private int inv_depth;
	private SymCoord sym;
	private RawCoord[] raws;

	private byte[] table;

	private PruningTable(){}

	public PruningTable(SymCoord sym, RawCoord raw, int n_moves, int inv_depth){
		this.sym = sym;
		raws = new RawCoord[1];
		raws[0] = raw;

		this.n_moves = n_moves;
		this.inv_depth = inv_depth;

		n_size = this.sym.getSize();
		for (RawCoord raw1 : raws) {
			n_size *= raw1.getSize();
		}
	}

	public PruningTable(SymCoord sym, RawCoord raw, RawCoord raw2, int n_moves, int inv_depth){
		this.sym = sym;
		raws = new RawCoord[2];
		raws[0] = raw;
		raws[1] = raw2;

		this.n_moves = n_moves;
		this.inv_depth = inv_depth;

		n_size = this.sym.getSize();
		for (RawCoord raw1 : raws) {
			n_size *= raw1.getSize();
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
		for (RawCoord raw : raws) {
			idx = idx * raw.getSize() + raw.conjugate(sym.sym);
		}
		return idx;
	}

	private void set( int idx ){
		for ( int i = raws.length - 1; i >= 0; i--){
			raws[i].coord = idx % raws[i].getSize();
			idx /= raws[i].getSize();
		}
		sym.coord = idx;
		sym.sym = 0;
	}

	private void normalise(){
		for (RawCoord raw : raws) {
			raw.coord = raw.conjugate(sym.sym);
		}
		sym.sym = 0;
	}

	public void initTable(){
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
		try{
			p.sym = sym.getClass().newInstance();
			p.raws = new RawCoord[raws.length];
			for ( int i = 0; i < raws.length; i++){
				p.raws[i] = raws[i].getClass().newInstance();
			}

		}
		catch (InstantiationException ignored) {
		}
		catch (IllegalAccessException ignored) {
		}

		/* Create the pruning table and fill with the solved states */
		table = new byte[(n_size+1)/2];
		for (int i = 0; i < (n_size+1)/2; i++)
			table[i] = -1;

		int done = sym.getSolvedStates().length;
		for (RawCoord raw1 : raws) done *= raw1.getSolvedStates().length;

		for (int d = 0; d < done; d++){
			int dd = d;
			for (RawCoord raw : raws) {
				raw.coord = raw.getSolvedStates()[dd % raw.getSolvedStates().length];
				dd /= raw.getSolvedStates().length;
			}
			sym.coord = sym.getSolvedStates()[dd];
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
