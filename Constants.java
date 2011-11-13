package fivestage444;

public class Constants{

	public static String datafiles_path = "/home/clement/Cube/programs/fivestage444/";

	public static int N_CORNER_ORIENT = 2187;

	public static int N_SQS_EDGE_PERM = 96*96*96;
	public static int N_SQS_CENTER_PERM = 12*12*12;
	public static int N_SQS_CORNER_PERM = 96;

	public static int N_EDGE_COMBO8 = 735471;	// 24!/(16!*8!)

	public static int N_STAGE2_EDGE_CONFIGS = 420;
	public static int N_STAGE2_CENTER_CONFIGS = 51482970;	// 24!/(16!*24*24)
	public static int N_CENTER_COMBO4 = 10626;

	public static int N_STAGE3_CENTER_CONFIGS = 900900;	//(16*15*14*13/24)*(12*11*10*9/24)
	public static int N_COMBO_16_8 = 12870;				//16!/(8!*8!)
	public static int N_STAGE3_EDGE_PAR = 2;
	public static int N_STAGE3_EDGE_CONFIGS = N_COMBO_16_8;	//16!/(8!*8!), does not include parity info

	public static int N_STAGE4_CENTER_CONFIGS = 70;	//8!/(4!*4!)
	public static int N_STAGE4_EDGE_CONFIGS = 88200;	//420*420/2
	public static int N_STAGE4_CORNER_CONFIGS = 420;	//8!/96
	public static int N_STAGE4_EDGE_HASH_TABLE = 200383;	//100153;
	public static int N_STAGE4_EDGE_HASH_DIVISOR = N_STAGE4_EDGE_HASH_TABLE - 2;	//also prime

	public static byte UP = 0;
	public static byte DOWN = 1;
	public static byte LEFT = 2;
	public static byte RIGHT = 3;
	public static byte FRONT = 4;
	public static byte BACK = 5;

//slice rotate codes
	public static int Uf  = 0;	//Up "face" (top slice) clockwise wrt top
	public static int Uf3 = 1;	//Up "face" counter-clockwise
	public static int Uf2 = 2;	//Up "face" 180 degrees

	public static int Us  = 3;	//Up "slice" (upper inner slice) clockwise wrt top
	public static int Us3 = 4;	//Up "slice" counter-clockwise
	public static int Us2 = 5;	//Up "slice" 180 degrees

	public static int Df  = 6;	//Down "face" (bottom slice) clockwise wrt bottom
	public static int Df3 = 7;	//Down "face" counter-clockwise
	public static int Df2 = 8;	//Down "face" 180 degrees

	public static int Ds  = 9;	//Down "slice" (lower inner slice) clockwise wrt bottom
	public static int Ds3 = 10;	//Down "slice" counter-clockwise
	public static int Ds2 = 11;	//Down "slice" 180 degrees

	public static int Lf  = 12;	//Left "face" (left-hand outer slice) clockwise wrt left side
	public static int Lf3 = 13;	//Left "face" counter-clockwise
	public static int Lf2 = 14;	//Left "face" 180 degrees

	public static int Ls  = 15;	//Left "slice" (left-hand inner slice) clockwise wrt left side
	public static int Ls3 = 16;	//Left "slice" counter-clockwise
	public static int Ls2 = 17;	//Left "slice" 180 degrees

	public static int Rf  = 18;	//Right "face" (right-hand outer slice) clockwise wrt right side
	public static int Rf3 = 19;	//Right "face" counter-clockwise
	public static int Rf2 = 20;	//Right "face" 180 degrees

	public static int Rs  = 21;	//Right "slice" (right-hand inner slice) clockwise wrt right side
	public static int Rs3 = 22;	//Right "slice" counter-clockwise
	public static int Rs2 = 23;	//Right "slice" 180 degrees

	public static int Ff  = 24;	//Front "face" (front outer slice) clockwise wrt front
	public static int Ff3 = 25;	//Front "face" counter-clockwise
	public static int Ff2 = 26;	//Front "face" 180 degrees

	public static int Fs  = 27;	//Front "slice" (front inner slice) clockwise wrt front
	public static int Fs3 = 28;	//Front "slice" counter-clockwise
	public static int Fs2 = 29;	//Front "slice" 180 degrees

	public static int Bf  = 30;	//Back "face" (rear outer slice) clockwise wrt back side
	public static int Bf3 = 31;	//Back "face" counter-clockwise
	public static int Bf2 = 32;	//Back "face" 180 degrees

	public static int Bs  = 33;	//Back "slice" (rear inner slice) clockwise wrt back side
	public static int Bs3 = 34;	//Back "slice" counter-clockwise
	public static int Bs2 = 35;	//Back "slice" 180 degrees

	public static int N_BASIC_MOVES  = Bs2 + 1;	//last rotate code plus one

//Twist moves (that are not also slice moves)
	public static int Ufs = 36;		//(Uu)
	public static int Ufs3 = 37;	//(Uu)'
	public static int Ufs2 = 38;	//(Uu)2
	public static int Dfs = 39;		//(Dd)
	public static int Dfs3 = 40;	//(Dd)'
	public static int Dfs2 = 41;	//(Dd)2
	public static int Lfs = 42;		//(Ll)
	public static int Lfs3 = 43;	//(Ll)'
	public static int Lfs2 = 44;	//(Ll)2
	public static int Rfs = 45;		//(Rr)
	public static int Rfs3 = 46;	//(Rr)'
	public static int Rfs2 = 47;	//(Rr)2
	public static int Ffs = 48;		//(Ff)
	public static int Ffs3 = 49;	//(Ff)'
	public static int Ffs2 = 50;	//(Ff)2
	public static int Bfs = 51;		//(Bb)
	public static int Bfs3 = 52;	//(Bb)'
	public static int Bfs2 = 53;	//(Bb)2

//Block moves (that are not also slice or twist moves)
	public static int UsDs3 = 54;
	public static int Us3Ds = 55;
	public static int Us2Ds2 = 56;
	public static int LsRs3 = 57;
	public static int Ls3Rs = 58;
	public static int Ls2Rs2 = 59;
	public static int FsBs3 = 60;
	public static int Fs3Bs = 61;
	public static int Fs2Bs2 = 62;

	public static int N_SQMOVES = 12;

	public static int N_FACE_MOVES = 18;

	public static int basic_to_face[] = {
	 0,  1,  2, -1, -1, -1,  3,  4,  5, -1, -1, -1,
	 6,  7,  8, -1, -1, -1,  9, 10, 11, -1, -1, -1,
	12, 13, 14, -1, -1, -1, 15, 16, 17, -1, -1, -1
	};

	public static int N_SQ_TWIST_MOVES = 12;
	public static int sq_twist_moves[][] = {
	{ Uf2/3,    -1 },
	{ Uf2/3, Us2/3 },
	{ Df2/3, Ds2/3 },
	{ Df2/3,    -1 },
	{ Lf2/3,    -1 },
	{ Lf2/3, Ls2/3 },
	{ Rf2/3, Rs2/3 },
	{ Rf2/3,    -1 },
	{ Ff2/3,    -1 },
	{ Ff2/3, Fs2/3 },
	{ Bf2/3, Bs2/3 },
	{ Bf2/3,    -1 }
	};

	public static int N_SQ_BLOCK_MOVES = 21;
	public static int sq_block_moves[][] = {
	{ Uf2/3,    -1, -1 },
	{ Us2/3,    -1, -1 },
	{ Uf2/3, Us2/3, -1 },
	{ Df2/3, Ds2/3, -1 },
	{ Ds2/3,    -1, -1 },
	{ Df2/3,    -1, -1 },
	{ Lf2/3,    -1, -1 },
	{ Ls2/3,    -1, -1 },
	{ Lf2/3, Ls2/3, -1 },
	{ Rf2/3, Rs2/3, -1 },
	{ Rs2/3,    -1, -1 },
	{ Rf2/3,    -1, -1 },
	{ Ff2/3,    -1, -1 },
	{ Fs2/3,    -1, -1 },
	{ Ff2/3, Fs2/3, -1 },
	{ Bf2/3, Bs2/3, -1 },
	{ Bs2/3,    -1, -1 },
	{ Bf2/3,    -1, -1 },
	{ Us2/3, Ds2/3, -1 },
	{ Ls2/3, Rs2/3, -1 },
	{ Fs2/3, Bs2/3, -1 }
	};

	public static int N_STAGE1_TWIST_MOVES_ALL = 54;
	public static int N_STAGE1_TWIST_MOVES = 36;
	public static int stage1_twist_moves[][] = {
	{ Uf,   -1, -1, -1 },
	{ Uf3,  -1, -1, -1 },
	{ Uf2,  -1, -1, -1 },
	{ Df,   -1, -1, -1 },
	{ Df3,  -1, -1, -1 },
	{ Df2,  -1, -1, -1 },
	{ Uf,   Us, -1, -1 },
	{ Uf3, Us3, -1, -1 },
	{ Uf2, Us2, -1, -1 },
	{ Df,   Ds, -1, -1 },
	{ Df3, Ds3, -1, -1 },
	{ Df2, Ds2, -1, -1 },

	{ Lf,    -1, -1, -1 },
	{ Lf3,   -1, -1, -1 },
	{ Lf2,   -1, -1, -1 },
	{ Rf,    -1, -1, -1 },
	{ Rf3,   -1, -1, -1 },
	{ Rf2,   -1, -1, -1 },
	{ Lf,    Ls, -1, -1 },
	{ Lf3,  Ls3, -1, -1 },
	{ Lf2,  Ls2, -1, -1 },
	{ Rf,    Rs, -1, -1 },
	{ Rf3,  Rs3, -1, -1 },
	{ Rf2,  Rs2, -1, -1 },

	{ Ff,    -1, -1, -1 },
	{ Ff3,   -1, -1, -1 },
	{ Ff2,   -1, -1, -1 },
	{ Bf,    -1, -1, -1 },
	{ Bf3,   -1, -1, -1 },
	{ Bf2,   -1, -1, -1 },
	{ Ff,    Fs, -1, -1 },
	{ Ff3,  Fs3, -1, -1 },
	{ Ff2,  Fs2, -1, -1 },
	{ Bf,    Bs, -1, -1 },
	{ Bf3,  Bs3, -1, -1 },
	{ Bf2,  Bs2, -1, -1 },

	{ Uf,   Us, Ds3, -1 },
	{ Uf3, Us3, Ds, -1 },
	{ Uf2, Us2, Ds2, -1 },
	{ Df,   Ds, Us3, -1 },
	{ Df3, Ds3, Us, -1 },
	{ Df2, Ds2, Us2, -1 },

	{ Lf,    Ls, Rs3, -1 },
	{ Lf3,  Ls3, Rs, -1 },
	{ Lf2,  Ls2, Rs2, -1 },
	{ Rf,    Rs, Ls3, -1 },
	{ Rf3,  Rs3, Ls, -1 },
	{ Rf2,  Rs2, Ls2, -1 },

	{ Ff,    Fs, Bs3, -1 },
	{ Ff3,  Fs3, Bs, -1 },
	{ Ff2,  Fs2, Bs2, -1 },
	{ Bf,    Bs, Fs3, -1 },
	{ Bf3,  Bs3, Fs, -1 },
	{ Bf2,  Bs2, Fs2, -1 }
	};

	public static int N_STAGE1_BLOCK_MOVES = 63;
	public static int N_STAGE1_BLOCK_MOVES_ALL = 81;
	public static int stage1_block_moves[][] = {
	{ Uf,   -1, -1, -1 },
	{ Uf3,  -1, -1, -1 },
	{ Uf2,  -1, -1, -1 },
	{ Us,   -1, -1, -1 },
	{ Us3,  -1, -1, -1 },
	{ Us2,  -1, -1, -1 },
	{ Df,   -1, -1, -1 },
	{ Df3,  -1, -1, -1 },
	{ Df2,  -1, -1, -1 },
	{ Ds,   -1, -1, -1 },
	{ Ds3,  -1, -1, -1 },
	{ Ds2,  -1, -1, -1 },
	{ Uf,   Us, -1, -1 },
	{ Uf3, Us3, -1, -1 },
	{ Uf2, Us2, -1, -1 },
	{ Df,   Ds, -1, -1 },
	{ Df3, Ds3, -1, -1 },
	{ Df2, Ds2, -1, -1 },
	{ Us,  Ds3, -1, -1 },
	{ Us3,  Ds, -1, -1 },
	{ Us2, Ds2, -1, -1 },

	{ Lf,   -1, -1, -1 },
	{ Lf3,  -1, -1, -1 },
	{ Lf2,  -1, -1, -1 },
	{ Ls,   -1, -1, -1 },
	{ Ls3,  -1, -1, -1 },
	{ Ls2,  -1, -1, -1 },
	{ Rf,   -1, -1, -1 },
	{ Rf3,  -1, -1, -1 },
	{ Rf2,  -1, -1, -1 },
	{ Rs,   -1, -1, -1 },
	{ Rs3,  -1, -1, -1 },
	{ Rs2,  -1, -1, -1 },
	{ Lf,   Ls, -1, -1 },
	{ Lf3, Ls3, -1, -1 },
	{ Lf2, Ls2, -1, -1 },
	{ Rf,   Rs, -1, -1 },
	{ Rf3, Rs3, -1, -1 },
	{ Rf2, Rs2, -1, -1 },
	{ Ls,  Rs3, -1, -1 },
	{ Ls3,  Rs, -1, -1 },
	{ Ls2, Rs2, -1, -1 },

	{ Ff,   -1, -1, -1 },
	{ Ff3,  -1, -1, -1 },
	{ Ff2,  -1, -1, -1 },
	{ Fs,   -1, -1, -1 },
	{ Fs3,  -1, -1, -1 },
	{ Fs2,  -1, -1, -1 },
	{ Bf,   -1, -1, -1 },
	{ Bf3,  -1, -1, -1 },
	{ Bf2,  -1, -1, -1 },
	{ Bs,   -1, -1, -1 },
	{ Bs3,  -1, -1, -1 },
	{ Bs2,  -1, -1, -1 },
	{ Ff,   Fs, -1, -1 },
	{ Ff3, Fs3, -1, -1 },
	{ Ff2, Fs2, -1, -1 },
	{ Bf,   Bs, -1, -1 },
	{ Bf3, Bs3, -1, -1 },
	{ Bf2, Bs2, -1, -1 },
	{ Fs,  Bs3, -1, -1 },
	{ Fs3,  Bs, -1, -1 },
	{ Fs2, Bs2, -1, -1 },

	{ Uf,   Us, Ds3, -1 },
	{ Uf3, Us3, Ds, -1 },
	{ Uf2, Us2, Ds2, -1 },
	{ Df,   Ds, Us3, -1 },
	{ Df3, Ds3, Us, -1 },
	{ Df2, Ds2, Us2, -1 },

	{ Lf,   Ls, Rs3, -1 },
	{ Lf3, Ls3, Rs, -1 },
	{ Lf2, Ls2, Rs2, -1 },
	{ Rf,   Rs, Ls3, -1 },
	{ Rf3, Rs3, Ls, -1 },
	{ Rf2, Rs2, Ls2, -1 },

	{ Ff,   Fs, Bs3, -1 },
	{ Ff3, Fs3, Bs, -1 },
	{ Ff2, Fs2, Bs2, -1 },
	{ Bf,   Bs, Fs3, -1 },
	{ Bf3, Bs3, Fs, -1 },

	{ Bf2, Bs2, Fs2, -1 }
	};

	public static int N_STAGE2_SLICE_MOVES = 28;
	public static int stage2_slice_moves[] = {
	Uf, Uf3, Uf2, Us, Us3, Us2,
	Df, Df3, Df2, Ds, Ds3, Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
	};

	public static int stg2_Uf = 0; //TODO: Use enum type.
	public static int stg2_Uf3 = 1;
	public static int stg2_Uf2 = 2;
	public static int stg2_Us = 3;
	public static int stg2_Us3 = 4;
	public static int stg2_Us2 = 5;
	public static int stg2_Df = 6;
	public static int stg2_Df3 = 7;
	public static int stg2_Df2 = 8;
	public static int stg2_Ds = 9;
	public static int stg2_Ds3 = 10;
	public static int stg2_Ds2 = 11;
	public static int stg2_Lf2 = 12;
	public static int stg2_Ls = 13;
	public static int stg2_Ls3 = 14;
	public static int stg2_Ls2 = 15;
	public static int stg2_Rf2 = 16;
	public static int stg2_Rs = 17;
	public static int stg2_Rs3 = 18;
	public static int stg2_Rs2 = 19;
	public static int stg2_Ff2 = 20;
	public static int stg2_Fs = 21;
	public static int stg2_Fs3 = 22;
	public static int stg2_Fs2 = 23;
	public static int stg2_Bf2 = 24;
	public static int stg2_Bs = 25;
	public static int stg2_Bs3 = 26;
	public static int stg2_Bs2 = 27;

	public static int N_STAGE2_TWIST_MOVES = 20;
	public static int stage2_twist_moves[][] = {
	{ stg2_Uf,        -1,       -1, -1 },
	{ stg2_Uf3,       -1,       -1, -1 },
	{ stg2_Uf2,       -1,       -1, -1 },
	{ stg2_Df,        -1,       -1, -1 },
	{ stg2_Df3,       -1,       -1, -1 },
	{ stg2_Df2,       -1,       -1, -1 },
	{ stg2_Uf,   stg2_Us,       -1, -1 },
	{ stg2_Uf3, stg2_Us3,       -1, -1 },
	{ stg2_Uf2, stg2_Us2,       -1, -1 },
	{ stg2_Df,   stg2_Ds,       -1, -1 },
	{ stg2_Df3, stg2_Ds3,       -1, -1 },
	{ stg2_Df2, stg2_Ds2,       -1, -1 },

	{ stg2_Lf2,       -1,       -1, -1 },
	{ stg2_Rf2,       -1,       -1, -1 },
	{ stg2_Lf2, stg2_Ls2,       -1, -1 },
	{ stg2_Rf2, stg2_Rs2,       -1, -1 },

	{ stg2_Ff2,       -1,       -1, -1 },
	{ stg2_Bf2,       -1,       -1, -1 },
	{ stg2_Ff2, stg2_Fs2,       -1, -1 },
	{ stg2_Bf2, stg2_Bs2,       -1, -1 },

	{ stg2_Uf,   stg2_Us, stg2_Ds3, -1 },
	{ stg2_Uf3, stg2_Us3,  stg2_Ds, -1 },
	{ stg2_Uf2, stg2_Us2, stg2_Ds2, -1 },
	{ stg2_Df,   stg2_Ds, stg2_Us3, -1 },
	{ stg2_Df3, stg2_Ds3,  stg2_Us, -1 },
	{ stg2_Df2, stg2_Ds2, stg2_Us2, -1 },

	{ stg2_Lf2, stg2_Ls2, stg2_Rs2, -1 },
	{ stg2_Rf2, stg2_Rs2, stg2_Ls2, -1 },

	{ stg2_Ff2, stg2_Fs2, stg2_Bs2, -1 },
	{ stg2_Bf2, stg2_Bs2, stg2_Fs2, -1 }
	};

//Also allow 2-twist moves such as (Ff) F which is the same as F2 f.
	public static int N_STAGE2_2TWIST_MOVES = 16;	// number of slice moves that can not be done by two allowed twist moves
	public static int stage2_2twist_moves[][] = {
	{ stg2_Fs, -1, -1 },
	{ stg2_Fs3, -1, -1 },
	{ stg2_Bs, -1, -1 },
	{ stg2_Bs3, -1, -1 },
	{ stg2_Ff2, stg2_Fs, -1 },
	{ stg2_Ff2, stg2_Fs3, -1 },
	{ stg2_Bf2, stg2_Bs, -1 },
	{ stg2_Bf2, stg2_Bs3, -1 },
	{ stg2_Ls, -1, -1 },
	{ stg2_Ls3, -1, -1 },
	{ stg2_Rs, -1, -1 },
	{ stg2_Rs3, -1, -1 },
	{ stg2_Lf2, stg2_Ls, -1 },
	{ stg2_Lf2, stg2_Ls3, -1 },
	{ stg2_Rf2, stg2_Rs, -1 },
	{ stg2_Rf2, stg2_Rs3, -1 }
	};



	public static int N_STAGE2_BLOCK_MOVES = 47;	//formerly 41
	public static int stage2_block_moves[][] = {
	{ stg2_Uf,        -1, -1, -1 },
	{ stg2_Uf3,       -1, -1, -1 },
	{ stg2_Uf2,       -1, -1, -1 },
	{ stg2_Us,        -1, -1, -1 },
	{ stg2_Us3,       -1, -1, -1 },
	{ stg2_Us2,       -1, -1, -1 },
	{ stg2_Df,        -1, -1, -1 },
	{ stg2_Df3,       -1, -1, -1 },
	{ stg2_Df2,       -1, -1, -1 },
	{ stg2_Ds,        -1, -1, -1 },
	{ stg2_Ds3,       -1, -1, -1 },
	{ stg2_Ds2,       -1, -1, -1 },
	{ stg2_Uf,   stg2_Us, -1, -1 },
	{ stg2_Uf3, stg2_Us3, -1, -1 },
	{ stg2_Uf2, stg2_Us2, -1, -1 },
	{ stg2_Df,   stg2_Ds, -1, -1 },
	{ stg2_Df3, stg2_Ds3, -1, -1 },
	{ stg2_Df2, stg2_Ds2, -1, -1 },
	{ stg2_Us,  stg2_Ds3, -1, -1 },
	{ stg2_Us3,  stg2_Ds, -1, -1 },
	{ stg2_Us2, stg2_Ds2, -1, -1 },

	{ stg2_Lf2,       -1, -1, -1 },
	{ stg2_Ls,        -1, -1, -1 },
	{ stg2_Ls3,       -1, -1, -1 },
	{ stg2_Ls2,       -1, -1, -1 },
	{ stg2_Rf2,       -1, -1, -1 },
	{ stg2_Rs,        -1, -1, -1 },
	{ stg2_Rs3,       -1, -1, -1 },
	{ stg2_Rs2,       -1, -1, -1 },
	{ stg2_Lf2, stg2_Ls2, -1, -1 },
	{ stg2_Rf2, stg2_Rs2, -1, -1 },
	{ stg2_Ls,  stg2_Rs3, -1, -1 },
	{ stg2_Ls3,  stg2_Rs, -1, -1 },
	{ stg2_Ls2, stg2_Rs2, -1, -1 },

	{ stg2_Ff2,       -1, -1, -1 },
	{ stg2_Fs,        -1, -1, -1 },
	{ stg2_Fs3,       -1, -1, -1 },
	{ stg2_Fs2,       -1, -1, -1 },
	{ stg2_Bf2,       -1, -1, -1 },
	{ stg2_Bs,        -1, -1, -1 },
	{ stg2_Bs3,       -1, -1, -1 },
	{ stg2_Bs2,       -1, -1, -1 },
	{ stg2_Ff2, stg2_Fs2, -1, -1 },
	{ stg2_Bf2, stg2_Bs2, -1, -1 },
	{ stg2_Fs,  stg2_Bs3, -1, -1 },
	{ stg2_Fs3,  stg2_Bs, -1, -1 },
	{ stg2_Fs2, stg2_Bs2, -1, -1 }
	};



	public static int N_STAGE3_SLICE_MOVES = 20;
	public static int stage3_slice_moves[] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
	};

	public static int stg3_Uf = 0; // TODO: Use enum type
	public static int stg3_Uf3 = 1;
	public static int stg3_Uf2 = 2;
	public static int stg3_Us2 = 3;
	public static int stg3_Df = 4;
	public static int stg3_Df3 = 5;
	public static int stg3_Df2 = 6;
	public static int stg3_Ds2 = 7;
	public static int stg3_Lf2 = 8;
	public static int stg3_Ls2 = 9;
	public static int stg3_Rf2 = 10;
	public static int stg3_Rs2 = 11;
	public static int stg3_Ff2 = 12;
	public static int stg3_Fs = 13;
	public static int stg3_Fs3 = 14;
	public static int stg3_Fs2 = 15;
	public static int stg3_Bf2 = 16;
	public static int stg3_Bs = 17;
	public static int stg3_Bs3 = 18;
	public static int stg3_Bs2 = 19;

	public static int N_STAGE3_TWIST_MOVES = 16;
	public static int stage3_twist_moves[][] = {
	{ stg3_Uf,        -1,       -1, -1 },
	{ stg3_Uf3,       -1,       -1, -1 },
	{ stg3_Uf2,       -1,       -1, -1 },
	{ stg3_Df,        -1,       -1, -1 },
	{ stg3_Df3,       -1,       -1, -1 },
	{ stg3_Df2,       -1,       -1, -1 },
	{ stg3_Uf2, stg3_Us2,       -1, -1 },
	{ stg3_Df2, stg3_Ds2,       -1, -1 },

	{ stg3_Lf2,       -1,       -1, -1 },
	{ stg3_Rf2,       -1,       -1, -1 },
	{ stg3_Lf2, stg3_Ls2,       -1, -1 },
	{ stg3_Rf2, stg3_Rs2,       -1, -1 },

	{ stg3_Ff2,       -1,       -1, -1 },
	{ stg3_Bf2,       -1,       -1, -1 },
	{ stg3_Ff2, stg3_Fs2,       -1, -1 },
	{ stg3_Bf2, stg3_Bs2,       -1, -1 }

	};

	public static int N_STAGE3_2TWIST_MOVES = 8;	// number of slice or slice+half-turn-face moves that can not be done by two allowed twist moves
	public static int stage3_2twist_moves[][] = {
	{ stg3_Fs, -1 },
	{ stg3_Fs3, -1 },
	{ stg3_Bs, -1 },
	{ stg3_Bs3, -1 },
	{ stg3_Ff2, stg3_Fs },
	{ stg3_Ff2, stg3_Fs3 },
	{ stg3_Bf2, stg3_Bs },
	{ stg3_Bf2, stg3_Bs3 },
	};

	public static int stage3_twist_map1[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3
	};

	public static int stage3_twist_map2[] = {
	-1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3
	};

	public static int N_STAGE3_BLOCK_MOVES = 31;
	public static int stage3_block_moves[][] = {
	{ stg3_Uf,        -1, -1, -1 },
	{ stg3_Uf3,       -1, -1, -1 },
	{ stg3_Uf2,       -1, -1, -1 },
	{ stg3_Us2,       -1, -1, -1 },
	{ stg3_Df,        -1, -1, -1 },
	{ stg3_Df3,       -1, -1, -1 },
	{ stg3_Df2,       -1, -1, -1 },
	{ stg3_Ds2,       -1, -1, -1 },
	{ stg3_Uf2, stg3_Us2, -1, -1 },
	{ stg3_Df2, stg3_Ds2, -1, -1 },
	{ stg3_Us2, stg3_Ds2, -1, -1 },

	{ stg3_Lf2,       -1, -1, -1 },
	{ stg3_Ls2,       -1, -1, -1 },
	{ stg3_Rf2,       -1, -1, -1 },
	{ stg3_Rs2,       -1, -1, -1 },
	{ stg3_Lf2, stg3_Ls2, -1, -1 },
	{ stg3_Rf2, stg3_Rs2, -1, -1 },
	{ stg3_Ls2, stg3_Rs2, -1, -1 },

	{ stg3_Ff2,       -1, -1, -1 },
	{ stg3_Fs,        -1, -1, -1 },
	{ stg3_Fs3,       -1, -1, -1 },
	{ stg3_Fs2,       -1, -1, -1 },
	{ stg3_Bf2,       -1, -1, -1 },
	{ stg3_Bs,        -1, -1, -1 },
	{ stg3_Bs3,       -1, -1, -1 },
	{ stg3_Bs2,       -1, -1, -1 },
	{ stg3_Ff2, stg3_Fs2, -1, -1 },
	{ stg3_Bf2, stg3_Bs2, -1, -1 },
	{ stg3_Fs,  stg3_Bs3, -1, -1 },
	{ stg3_Fs3,  stg3_Bs, -1, -1 },
	{ stg3_Fs2, stg3_Bs2, -1, -1 }

	};

	public static int N_STAGE4_SLICE_MOVES = 16;
	public static int stage4_slice_moves[] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs2, Bf2, Bs2
	};

	public static int stg4_Uf = 0;
	public static int stg4_Uf3 = 1;
	public static int stg4_Uf2 = 2;
	public static int stg4_Us2 = 3;
	public static int stg4_Df = 4;
	public static int stg4_Df3 = 5;
	public static int stg4_Df2 = 6;
	public static int stg4_Ds2 = 7;
	public static int stg4_Lf2 = 8;
	public static int stg4_Ls2 = 9;
	public static int stg4_Rf2 = 10;
	public static int stg4_Rs2 = 11;
	public static int stg4_Ff2 = 12;
	public static int stg4_Fs2 = 13;
	public static int stg4_Bf2 = 14;
	public static int stg4_Bs2 = 15;

	public static int N_STAGE4_TWIST_MOVES = 16;
	public static int stage4_twist_moves[][] = {
	{ stg4_Uf,        -1,       -1, -1 },
	{ stg4_Uf3,       -1,       -1, -1 },
	{ stg4_Uf2,       -1,       -1, -1 },
	{ stg4_Df,        -1,       -1, -1 },
	{ stg4_Df3,       -1,       -1, -1 },
	{ stg4_Df2,       -1,       -1, -1 },
	{ stg4_Uf2, stg4_Us2,       -1, -1 },
	{ stg4_Df2, stg4_Ds2,       -1, -1 },

	{ stg4_Lf2,       -1,       -1, -1 },
	{ stg4_Rf2,       -1,       -1, -1 },
	{ stg4_Lf2, stg4_Ls2,       -1, -1 },
	{ stg4_Rf2, stg4_Rs2,       -1, -1 },

	{ stg4_Ff2,       -1,       -1, -1 },
	{ stg4_Bf2,       -1,       -1, -1 },
	{ stg4_Ff2, stg4_Fs2,       -1, -1 },
	{ stg4_Bf2, stg4_Bs2,       -1, -1 }

	};

	public static int N_STAGE4_BLOCK_MOVES = 25;
	public static int stage4_block_moves[][] = {
	{ stg4_Uf,        -1, -1, -1 },
	{ stg4_Uf3,       -1, -1, -1 },
	{ stg4_Uf2,       -1, -1, -1 },
	{ stg4_Us2,       -1, -1, -1 },
	{ stg4_Df,        -1, -1, -1 },
	{ stg4_Df3,       -1, -1, -1 },
	{ stg4_Df2,       -1, -1, -1 },
	{ stg4_Ds2,       -1, -1, -1 },
	{ stg4_Uf2, stg4_Us2, -1, -1 },
	{ stg4_Df2, stg4_Ds2, -1, -1 },
	{ stg4_Us2, stg4_Ds2, -1, -1 },

	{ stg4_Lf2,       -1, -1, -1 },
	{ stg4_Ls2,       -1, -1, -1 },
	{ stg4_Rf2,       -1, -1, -1 },
	{ stg4_Rs2,       -1, -1, -1 },
	{ stg4_Lf2, stg4_Ls2, -1, -1 },
	{ stg4_Rf2, stg4_Rs2, -1, -1 },
	{ stg4_Ls2, stg4_Rs2, -1, -1 },

	{ stg4_Ff2,       -1, -1, -1 },
	{ stg4_Fs2,       -1, -1, -1 },
	{ stg4_Bf2,       -1, -1, -1 },
	{ stg4_Bs2,       -1, -1, -1 },
	{ stg4_Ff2, stg4_Fs2, -1, -1 },
	{ stg4_Bf2, stg4_Bs2, -1, -1 },
	{ stg4_Fs2, stg4_Bs2, -1, -1 }

	};

	public static int get_dist_4bit (int x, byte[] p)
	{
		int x2 = x >> 1;
		int j = x & 0x1;
		if (j == 0) {
			return p[x2] & 0xF;
		}
		return (p[x2] >> 4) & 0xF;
	}

	public static int perm_n_pack (int n, byte[] array_in, int offset)
	{
		int idx;
		int i, j;

		idx = 0;

		for (i = 0; i < n; ++i) {
			idx *= (n - i);

			for (j = i + 1; j < n; ++j) {
				if (array_in[j+offset] < array_in[i+offset]) {
					++idx;
				}
			}
		}
		return idx;
	}

	public static void perm_n_unpack (int n, int idx, byte[] array_out, int offset)
	{
		int i, j;

		for (i = n - 1; i >= 0; --i) {
			array_out[i+offset] = (byte)(idx % (n - i));
			idx /= (n - i);

			for (j = i + 1; j < n; ++j) {
				if (array_out[j+offset] >= array_out[i+offset]) {
					array_out[j+offset]++;
				}
			}
		}
	}

	public static int STAGE3_NUM_SOLVED_CENTER_CONFIGS = 12;
	public static int stage3_solved_centers[] = {
	900830, 900844,	900850, 900853, 900857, 900858,
	900871, 900872, 900876,	900879, 900885, 900899
	};

	public static int STAGE4_NUM_SOLVED_CENTER_CONFIGS = 12;
	public static short stage4_solved_centers_bm[] = {
	0x0F, 0xF0, 0x55, 0xAA, 0x5A, 0xA5, 0x69, 0x96, 0x66, 0x99, 0x3C, 0xC3
	};

	public interface DoMove{
		public int do_move(int idx, int move_code); 
	}

}
