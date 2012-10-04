package cg.fivestage444;

import cg.fivestage444.Coordinates.RawCoord;
import cg.fivestage444.Coordinates.SymCoord;

import java.io.*;


public class PruningTable {

	int n_moves;
	int n_size;
	SymCoord sym;
	RawCoord[] raws;

	byte[] table;

	public PruningTable(){}

	public PruningTable(SymCoord sym, RawCoord raw, int n_moves){
		this.sym = sym;
		raws = new RawCoord[1];
		raws[0] = raw;

		this.n_moves = n_moves;

		n_size = this.sym.getSize();
		for ( int i = 0; i < raws.length; i++){
			n_size *= raws[i].getSize();
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
		for ( int i = 0; i < raws.length; i++){
			idx = idx * raws[i].getSize() + raws[i].conjugate(sym.sym);
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
		for ( int i = 0; i < raws.length; i++){
			raws[i].coord = raws[i].conjugate(sym.sym);
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
		final int INV_DEPTH = 7;

		/* Create a new instance of PruningTable with the same classes in it */
		PruningTable p = new PruningTable();
		try{
			p.sym = sym.getClass().newInstance();
			p.raws = new RawCoord[raws.length];
			for ( int i = 0; i < raws.length; i++){
				p.raws[i] = raws[i].getClass().newInstance();
			}

		}
		catch (InstantiationException e) {
		}
		catch (IllegalAccessException e) {
		}

		/* Create the pruning table and fill with the solved states */
		table = new byte[(n_size+1)/2];
		for (int i = 0; i < (n_size+1)/2; i++)
			table[i] = -1;

		int done = sym.getSolvedStates().length;
		for (int i = 0; i < raws.length; i++)
			done *= raws[i].getSolvedStates().length;

		for (int d = 0; d < done; d++){
			int dd = d;
			for (int i = 0; i < raws.length; i++){
				raws[i].coord = raws[i].getSolvedStates()[dd % raws[i].getSolvedStates().length];
				dd /= raws[i].getSolvedStates().length;
			}
			sym.coord = sym.getSolvedStates()[dd];
			sym.sym = 0;
			writeTable(get(), 0);
		}

		/* Build the table */
		int depth = 0;
		while (( done < n_size ) && ( depth < 15 )) {
			boolean inv = depth > INV_DEPTH;
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
						long symS = p.sym.getSyms();
						p.normalise();
						for (int k=0; symS != 0; symS>>=1, k++) {
							if ((symS & 0x1L) == 0) continue;
							p.sym.sym = k;
							int sym_idx = p.get();
							if( sym_idx == idx )
								nsym++;
							if (readTable(sym_idx) == 0x0f) {
								writeTable(sym_idx, depth);
								done++;
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
