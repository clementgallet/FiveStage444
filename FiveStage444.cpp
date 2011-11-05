// FiveStage444.exe.cpp : Defines the entry point for the console application.
//

#include <iostream>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <signal.h>

bool got_interrupt = false;

void SignalHandler(int signal)
{
	if (signal == SIGINT) {
		got_interrupt = true;
	}
}

// Program options (compile-time)
//#define USE_TITLE_BAR
//#define OUTPUT_DIAGNOSTICS
//#define WRITE_TABLE_FILES
//#define GEN_ONEBIT_FILES
//#define USE_CUBE_LIST_TABLE
#define USE_SYMMETRY
//#define GET_SUMMARY
#define DO_SOLVE_TEST
//#define DO_SOLVE_SQS
//#define DO_SOLVE_STAGE1
//#define DO_SOLVE_STAGE2
//#define DO_SOLVE_STAGE3
//#define DO_SOLVE_STAGE4
#define DO_SOLVE_WHOLE_CUBE
#define REGULAR_MOVES
//#define DRB_FIXED_MOVES
#define PRUNING_TABLES

#ifdef DO_SOLVE_WHOLE_CUBE
#define DO_SOLVE_STAGE1
#define DO_SOLVE_STAGE2
#define DO_SOLVE_STAGE3
#define DO_SOLVE_STAGE4
#define DO_SOLVE_SQS
#endif

#ifdef DO_SOLVE_SQS
#define SQUARES_INIT
#endif
#ifdef DO_SOLVE_STAGE1
#define STAGE1_INIT
#define ELOC_INIT
#endif

#ifdef DO_SOLVE_STAGE2
#define STAGE2_INIT
#define ELOC_INIT
#define CLOC_INIT
#endif

#ifdef DO_SOLVE_STAGE3
#define STAGE3_INIT
#define ELOC_INIT
#endif

#ifdef DO_SOLVE_STAGE4
#define STAGE4_INIT
#define ELOC_INIT
#endif


typedef unsigned int UINT;
typedef unsigned short USHORT;
typedef unsigned char UBYTE;
typedef UINT (*FUNC_PTR)(UINT,int);

// Revenge.cpp : Program to generate a table of distances for various subsets of states
// of the 4x4x4 Rubik's Revenge cube. The "squares group" is the first goal of the program.
//

// EDGE CONVENTION:

// There are 24 "edge" cubies, numbered 0 to 23.
// The home positions of these cubies are labeled in the diagram below.
// Each edge cubie has two exposed faces, so there are two faces labelled with
// each number.

//             -------------
//             |    5  1   |
//             |12   UP  10|
//             | 8       14|
//             |    0  4   |
// -------------------------------------------------
// |   12  8   |    0  4   |   14 10   |    1  5   |
// |22  LHS  16|16  FRT  21|21  RHS  19|19  BAK  22|
// |18       20|20       17|17       23|23       18|
// |    9 13   |    6  2   |   11 15   |    7  3   |
// -------------------------------------------------
//             |    6  2   |
//             |13  DWN  11|
//             | 9       15|
//             |    3  7   |
//             -------------


// There are 8 "corner" cubies, numbered 0 to 7.
// The home positions of these cubies are labeled in the diagram below.
// Each corner cubie has three exposed faces, so there are three faces labelled
// with each number. Asterisks mark the primary facelet position. Orientation
// will be the number of clockwise rotations the primary facelet is from the
// primary facelet position where it is located.

//            +----------+
//            |*3*    *2*|
//            |    UP    |
//            |*0*    *1*|
// +----------+----------+----------+----------+
// | 7      0 | 0      1 | 1      2 | 2      3 |
// |   LEFT   |  FRONT   |  RIGHT   |  BACK    |
// | 7      4 | 4      5 | 5      6 | 6      7 |
// +----------+----------+----------+----------+
//            |*4*    *5*|
//            |   DOWN   |
//            |*7*    *6*|
//            +----------+

//For squares calculation, corners are numbered as given below.
//This makes the corners look much like a set of 8 edges of a
//given pair of inner slices.
//            +----------+
//            | 5      1 |
//            |    UP    |
//            | 0      4 |
// +----------+----------+----------+----------+
// | 5      0 | 0      4 | 4      1 | 1      5 |
// |   LEFT   |  FRONT   |  RIGHT   |  BACK    |
// | 3      6 | 6      2 | 2      7 | 7      3 |
// +----------+----------+----------+----------+
//            | 6      2 |
//            |   DOWN   |
//            | 3      7 |
//            +----------+

// There are 24 "center" cubies. For the squares analysis, they are numbered 0 to 23 as shown.
//             -------------
//             |           |
//             |    2  3   |
//             |    0  1   |
//             |           |
// -------------------------------------------------
// |           |           |           |           |
// |    9  8   |   16 18   |   12 13   |   22 20   |
// |   11 10   |   17 19   |   14 15   |   23 21   |
// |           |           |           |           |
// -------------------------------------------------
//             |           |
//             |    4  5   |
//             |    6  7   |
//             |           |
//             -------------

// For the other analyses, they are numbered 0 to 23 as shown.
//             -------------
//             |           |
//             |    3  1   |
//             |    0  2   |
//             |           |
// -------------------------------------------------
// |           |           |           |           |
// |   10  8   |   16 19   |   14 12   |   21 22   |
// |    9 11   |   18 17   |   13 15   |   23 20   |
// |           |           |           |           |
// -------------------------------------------------
//             |           |
//             |    6  4   |
//             |    5  7   |
//             |           |
//             -------------


const int MAX_DISTANCE = 4;		//maximum distance value to be allowed to be generated.
const char default_datafile_path[] = "";

char datafiles_path[256];

typedef char Face;

const UINT N_CORNER_PERM = 40320;
const UINT N_CORNER_ORIENT = 2187;
const UINT N_EDGE_PERM_12_1 = 479001600;

const int N_CORNER_BM = 256;	//2**8

const int N_CUBESYM = 48;
const int N_ESYM = 16;
const int N_ANTISYM = 96;
const int N_SYMX = /* N_ANTISYM; // or */ N_CUBESYM;

const int N_CORNER_GROUPS = 984;
const int N_CGG_SIZE = 12;

const UINT N_SQS_EDGE_PERM = 96*96*96;
const UINT N_SQS_CENTER_PERM = 12*12*12;
const UINT N_SQS_CORNER_PERM = 96;
const UINT N_SQS_EDGE_SYMCOUNT = 21908;
const UINT N_SQS_EDGE_ANTISYM_COUNT = 12331; //not valid
//Antisymmetry appears to be invalid with indistinguishable centers

const UINT N_SYMCOUNT = (N_SYMX == N_ANTISYM ? N_SQS_EDGE_ANTISYM_COUNT : N_SQS_EDGE_SYMCOUNT);

const UINT N_SQS = N_SQS_EDGE_SYMCOUNT*N_SQS_CORNER_PERM*N_SQS_CENTER_PERM;
const UINT N_SQS_TABLE_SIZE = N_SQS/16u;

const UINT N_EDGE_COMBO8 = 735471;	// 24!/(16!*8!)
const UINT N_STAGE1 = N_EDGE_COMBO8*N_CORNER_ORIENT;
const UINT N_STAGE1_TABLE_SIZE = (N_STAGE1 + 15)/16;

const UINT N_SYM_STAGE2 = 8;
const UINT N_STAGE2_EDGE_CONFIGS = 420;
const UINT N_STAGE2_CENTER_CONFIGS = 51482970;	// 24!/(16!*24*24)
const UINT N_STAGE2_EDGE_SYMCONFIGS = 98;
const UINT N_STAGE2_CENTER_SYMCONFIGS = 6440445;
const UINT N_STAGE2_CEN_TABLE_SIZE = (N_STAGE2_CENTER_CONFIGS+31)/32;
const UINT N_STAGE2_TABLE_SIZE = N_STAGE2_EDGE_SYMCONFIGS*N_STAGE2_CEN_TABLE_SIZE;	//1 bit per position!
const UINT N_CENTER_COMBO4 = 10626;

const UINT N_SYM_STAGE3 = 8;
const UINT N_STAGE3_CENTER_CONFIGS = 900900;	//(16*15*14*13/24)*(12*11*10*9/24)
const UINT N_COMBO_16_8 = 12870;				//16!/(8!*8!)
const UINT N_STAGE3_EDGE_PAR = 2;
const UINT N_STAGE3_EDGE_CONFIGS = N_COMBO_16_8;	//16!/(8!*8!), does not include parity info
const UINT N_STAGE3_EDGE_SYMCONFIGS = 1763;
const UINT N_STAGE3 = N_STAGE3_EDGE_PAR*N_STAGE3_CENTER_CONFIGS * N_STAGE3_EDGE_SYMCONFIGS; //3,176,573,400
const UINT N_STAGE3_TABLE_SIZE = (N_STAGE3 + 15)/16;

const UINT N_SYM_STAGE4 = 16;
const UINT N_STAGE4_CENTER_CONFIGS = 70;	//8!/(4!*4!)
const UINT N_STAGE4_RAW_EDGE_CONFIGS = 40320*40320;		//176400;	//420*420 ?
const UINT N_STAGE4_EDGE_CONFIGS = 88200;	//420*420/2
const UINT N_STAGE4_RAW_EDGE_SOLVED_CONFIGS = 96*96;		//9216
const UINT N_STAGE4_EDGE_TABLE_SIZE = N_STAGE4_RAW_EDGE_CONFIGS/16;
const UINT N_STAGE4_CORNER_CONFIGS = 420;	//8!/96
const UINT N_STAGE4 = N_STAGE4_EDGE_CONFIGS * N_STAGE4_CENTER_CONFIGS * N_STAGE4_CORNER_CONFIGS; //2,593,080,000
const UINT N_STAGE4_TABLE_SIZE = N_STAGE4/16; //N_STAGE4_EDGE_CONFIGS_8 * N_STAGE4_CENTER_CONFIGS_2 * N_STAGE4_CORNER_CONFIGS;	//162,067,500
const UINT N_STAGE4_EDGE_HASH_TABLE = 200383;	//100153;
const UINT N_STAGE4_EDGE_HASH_DIVISOR = N_STAGE4_EDGE_HASH_TABLE - 2;	//also prime
const UINT N_STAGE4_EDGE_SYMCONFIGS = 5968;
const UINT N_STAGE4_SYM_REDUCED = N_STAGE4_EDGE_SYMCONFIGS * N_STAGE4_CENTER_CONFIGS * N_STAGE4_CORNER_CONFIGS;
const UINT N_STAGE4_SYM_TABLE_SIZE = (N_STAGE4_SYM_REDUCED + 15)/16;

const Face UP = 0;
const Face DOWN = 1;
const Face LEFT = 2;
const Face RIGHT = 3;
const Face FRONT = 4;
const Face BACK = 5;

char* face_names[6] = { "UP", "DOWN", "LEFT", "RIGHT", "FRONT", "BACK" };

//These are borrowed from an older program, different numbering scheme.
const Face FaceU = 0;
const Face FaceF = 1;
const Face FaceL = 2;
const Face FaceD = 3;
const Face FaceB = 4;
const Face FaceR = 5;

class Stab {
private:
	USHORT m_stab[N_SYMX/16];
public:
	Stab ();
	void set_bit (int n);
	bool test_bit (int n) const;
	bool is_equal (const Stab& s) const;
	const char* to_string () const;
};

Stab::Stab ()
{
	int i;
	for (i = 0; i < N_SYMX/16; ++i) {
		m_stab[i] = 0;
	}
}

void
Stab::set_bit (int n)
{
	m_stab[n/16] |= 1 << (n & 0xF);
}

bool
Stab::test_bit (int n) const
{
	return (m_stab[n/16] & (1 << (n & 0xF))) != 0;
}

bool
Stab::is_equal (const Stab& s) const
{
	int i;
	for (i = 0; i < N_SYMX/16; ++i) {
		if (m_stab[i] != s.m_stab[i]) {
			return false;
		}
	}
	return true;
}

const char*
Stab::to_string () const
{
	int i;
	static char s1[16];		//DANGER! uses static buffer
	for (i = 0; i < N_SYMX/16; ++i) {
		sprintf (&s1[4*i], "%04X", static_cast<int>(m_stab[2-i]));
	}
	s1[12] = '\0';
	return &s1[0];
}

Stab stab1;

class Bitvec {
	UINT* m_p;
	UINT m_n;
	Bitvec ();	//no default ctor
public:
	Bitvec (UINT N) {
		UINT i;
		UINT n = N/32u;
		if ((N % 32u) != 0) ++n;
		m_p = new UINT[n];
		m_n = N;
		for (i = 0; i < n; ++i) {
			m_p[i] = 0;
		}
	}
	virtual ~Bitvec () {
		delete [] m_p;
	}
	void setb (UINT b) {
		if (b < m_n) {
			int i = b >> 5;
			int j = b & 0x1F;
			m_p[i] |= (1 << j);
		}
	}
	void clrb (UINT b) {
		if (b < m_n) {
			int i = b >> 5;
			int j = b & 0x1F;
			m_p[i] &= ~(1 << j);
		}
	}
	bool tstb (UINT b) {
		if (b < m_n) {
			int i = b >> 5;
			int j = b & 0x1F;
			return (m_p[i] & (1 << j)) != 0;
		}
		return false;
	}
	void mergewith (const Bitvec& bv2) {
		UINT i;
		UINT n = m_n;
		if (bv2.m_n < n) {
			n = bv2.m_n;
		}
		UINT n2 = n >> 5;
		if ((n & 0x1F) != 0) {
			++n2;
		}
		for (i = 0; i < n2; ++i) {
			m_p[i] |= bv2.m_p[i];	//if bv2 bigger than "this" bv, could cause extraneous "1's" in last element.
		}
	}
};

UINT byte_clr_masks[4] = { 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0x00FFFFFF };
UINT pow3tab[5] = { 1, 3, 9, 27, 81 };

//TableIndex: class/structure used in accessing logical elements of the main array.
struct TableIndex5 {
	int m_word_index;
	int m_byte;
	int m_element;
	void init (UINT x)
	{
		UINT i = x / 20u;
		UINT j = x % 20u;
		m_word_index = i;
		m_byte = j / 5;
		m_element = j % 5;
	}
	void set_value (UINT* p, int x) const	//x = 0 (unknown), 1 (old), 2 (new)
	{
		//p is the base address of the array.
		UINT oldb = (p[m_word_index] >> (8*m_byte)) & 0xFF;
		UINT oldv = (oldb / pow3tab[m_element]) % 3;
		UINT newv = oldb - pow3tab[m_element]*oldv;
		newv += pow3tab[m_element]*x;
		p[m_word_index] &= byte_clr_masks[m_byte];
		p[m_word_index] |= newv << (8*m_byte);
	}
	int get_value (const UINT* p) const		//p is the base address of the array.
	{
		UINT oldb = (p[m_word_index] >> (8*m_byte)) & 0xFF;
		return (oldb / pow3tab[m_element]) % 3;
	}
};

struct TableIndex {
	int m_word_index;
	int m_element;
	void init (UINT x)
	{
		m_word_index = x / 16u;
		m_element = x % 16u;
	}
	void set_value (UINT* p, int x) const	//x = 0 (unknown), 1 (old), 2 (new)
	{
		//p is the base address of the array.
		p[m_word_index] &= ~(0x3 << (2*m_element));
		p[m_word_index] |= (x << (2*m_element));
	}
	int get_value (const UINT* p) const		//p is the base address of the array.
	{
		return (p[m_word_index] >> (2*m_element)) & 0x3;
	}
};

struct TableIndex10 {	//pack 10 3-bit elements per word
	int m_word_index;
	int m_element;
	void init (UINT x)
	{
		m_word_index = x / 10u;
		m_element = x % 10u;
	}
	void set_value (UINT* p, int x) const	//x = 0 (unknown), 1 (old), 2 (new)
	{
		//p is the base address of the array.
		p[m_word_index] &= ~(0x7 << (3*m_element));
		p[m_word_index] |= (x << (3*m_element));
	}
	int get_value (const UINT* p) const		//p is the base address of the array.
	{
		return (p[m_word_index] >> (3*m_element)) & 0x7;
	}
};

//CubeState structure: a cubie-level representation of the cube.
struct CubeState {
	int m_distance;		//distance from solved state
	Face m_edge[24];	//what's at each edge position
	Face m_cor[8];		//what's at each corner position (3*cubie + orientation)
	Face m_cen[24];		//what's at each center position
	void init ();
	void init_super ();
	bool compare (const CubeState& cube2) const;
	void init_sqs ();
	void do_move (int move_code);
	void do_sqs_move (int sqs_move_code);
	void compose_edge (const CubeState& cs1, const CubeState& cs2);
	void compose_corner (const CubeState& cs1, const CubeState& cs2);
	void invert_fbcen ();
	bool edgeUD_parity_odd () const;	//compute edge parity for stage 3 purposes.
	void print_super () const;
	bool check () const;
};

//CubeCoord represents the state of a cube with these coordinates:
//	m_ep1: where edges 0, 8, 16 are
//  m_ep2lr: where edges 1..7 are
//  m_ep2fb: where edges 9..15 are
//	m_ep2ud: where edges 17..23 are
//	m_cp: corner permutation coordinate
//  m_co: corner orientation coordinate
//  m_cen: 
//The structure contains a miscellaneous data parameter, normally used
//to store the distance from solved state, if applicable.
//Total size should be 12 bytes.
struct CubeCoord {
	UINT m_cen_ud;
	UINT m_cen_lr;
	UINT m_cen_fb;
	UINT m_ep2lr;
	UINT m_ep2fb;
	UINT m_ep2ud;
	USHORT m_ep1;
	USHORT m_cp;
	USHORT m_co;
	USHORT m_distance;
	void init ();
	bool compare (const CubeCoord& cube2) const;
#ifdef CLOC_INIT
	void set_from_std_cube (const CubeState& cube1);
	void convert_to_std_cube (CubeState* result_cube) const;
#endif
};

struct CubeSqsCoord {
	UINT m_ep96x96x96;
	USHORT m_cen12x12x12;
	UBYTE m_cp96;
	UBYTE m_distance;
	void init ();
	bool compare (const CubeSqsCoord& cube2) const;
	int cmp (const CubeSqsCoord& cube2) const;
	void do_move (int sqs_move_code);
	void do_whole_cube_move (int sqs_whole_cube_move);
	bool is_representative () const;
	bool is_representativeA () const;
	bool is_representativeB () const;
	void get_representative (CubeSqsCoord* rep) const;
	bool is_solved () const;
	bool check () const;
};

struct CubeSymSqsCoord {
	UINT m_ep_sym;	//96*epval+symval
	USHORT m_cen12x12x12;
	UBYTE m_cp96;
	UBYTE m_distance;
	void init ();
	bool compare (const CubeCoord& cube2) const;
	void do_move (int sqs_move_code);
	bool is_representative () const;
	void get_representative (CubeSqsCoord* rep) const;
	bool check () const;
};

struct CubeSuperSqsCoord {
	UINT m_ep96x96x96;
	UINT m_cen96x96x96;
	UBYTE m_cp96;
	UBYTE m_distance;
	void init ();
	bool compare (const CubeSuperSqsCoord& cube2) const;
	int cmp (const CubeSuperSqsCoord& cube2) const;
	void do_move (int sqs_move_code);
	void do_whole_cube_move (int sqs_whole_cube_move);
	bool is_representative () const;
	void get_representative (CubeSuperSqsCoord* rep) const;
	void print () const;
};

struct CubeSymSuperSqsCoord {
	UINT m_ep_sym;	//96*epval+symval
	UINT m_cen9x12x12;
	UBYTE m_cp96;
	UBYTE m_distance;
	void init ();
	bool compare (const CubeSymSuperSqsCoord& cube2) const;
	void do_move (int sqs_move_code);
	bool is_representative () const;
	void get_representative (CubeSuperSqsCoord* rep) const;
};

struct CubeStage1 {
	USHORT m_co;	//corner orientation
	UINT m_edge_ud_combo8;
	UBYTE m_distance;
	void init ();
	bool compare (const CubeStage1& cube2) const;
	int cmp (const CubeStage1& cube2) const;
	void do_move_slow (int move_code);
	void do_move (int move_code);
	void do_whole_cube_move (int whole_cube_move);
	bool is_representative () const;
	void get_representative (CubeStage1* rep, int* mult = NULL) const;
	bool is_solved () const;
	bool check () const;
};

struct CubeStage2 {
	UINT m_centerFB;
	USHORT m_edge;	//edge coordinate
	UBYTE m_distance;
	void init ();
	bool compare (const CubeStage2& cube2) const;
	int cmp (const CubeStage2& cube2) const;
	bool is_solved () const;
	void do_move_slow (int move_code);
	void do_move (int move_code);
	void do_whole_cube_move (int whole_cube_move);
	bool is_representative () const;
	bool is_representative1 () const;
	bool is_representative2 () const;
	void get_representative (CubeStage2* rep, int* mult = NULL) const;
	int distinct_count () const;
	void print () const;
};

struct CubeStage3 {
	UINT m_centerLR;
	USHORT m_edge;	//edge coordinate
	bool m_edge_odd;	//odd parity of edges?
	UBYTE m_distance;
	void init ();
	bool compare (const CubeStage3& cube2) const;
	int cmp (const CubeStage3& cube2) const;
	void do_move_slow (int move_code);
	void do_move (int move_code);
	bool is_representative () const;
	void get_representative (CubeStage3* rep, int* mult = NULL) const;
	int distinct_count () const;
	bool is_solved () const;
	void print () const;
};

struct CubeStage4 {
	USHORT m_centerUD;	//center coordinate (70)
	USHORT m_corner;	//corner coordinate	(420)
	UINT m_edge;	//edge coordinate (420*420)
	UBYTE m_distance;
	void init ();
	bool compare (const CubeStage4& cube2) const;
	int cmp (const CubeStage4& cube2) const;
	void do_move_slow (int move_code);
	void do_move (int move_code);
	bool is_representative () const;
	void get_representative (CubeStage4* rep, int* mult = NULL) const;
	int distinct_count () const;
	bool is_solved () const;
	void print () const;
};

#ifdef PRUNING_TABLES
class CubePruningTable {
private:
	UINT m_num_positions;
	UBYTE* m_ptable;
	void* m_do_move_func;
	int m_num_moves;
	int* m_move_list;
	int m_num_moves2;
	int* m_move_list2;
	int m_num_solved;
	int* m_psolved;
	int m_stage;
	int m_metric;
	UINT m_cencoredg;
	UINT m_count;
public:
	CubePruningTable (UINT num_positions, UBYTE* ptable, void* move_func, int stage, int metric, UINT cencoredg);
	~CubePruningTable ();
	void init_move_list (int dim2, int num_moves, int* move_list);
	void init_move_list2 (int dim2, int num_moves, int* move_list);
	void init_solved_list (int count, int* solved_list);
	void init ();
	void analyze ();
	void generate1 (UINT idx, int dist);
	void generate2 (UINT idx, int dist);
	bool check_new (UINT idx) const;
	void add_to_table (UINT idx, int dist);
	UINT get_count () { return m_count; }
};

//Class to create and clean up all pruning tables
class CubePruningTableMgr {
public:
	void delete_cpts ();
	~CubePruningTableMgr ()
	{
		delete_cpts ();
	}
	void init_pruning_tables (int metric);
} cpt_mgr;
#endif


//slice rotate codes
const int Uf  = 0;	//Up "face" (top slice) clockwise wrt top
const int Uf3 = 1;	//Up "face" counter-clockwise
const int Uf2 = 2;	//Up "face" 180 degrees

const int Us  = 3;	//Up "slice" (upper inner slice) clockwise wrt top
const int Us3 = 4;	//Up "slice" counter-clockwise
const int Us2 = 5;	//Up "slice" 180 degrees

const int Df  = 6;	//Down "face" (bottom slice) clockwise wrt bottom
const int Df3 = 7;	//Down "face" counter-clockwise
const int Df2 = 8;	//Down "face" 180 degrees

const int Ds  = 9;	//Down "slice" (lower inner slice) clockwise wrt bottom
const int Ds3 = 10;	//Down "slice" counter-clockwise
const int Ds2 = 11;	//Down "slice" 180 degrees

const int Lf  = 12;	//Left "face" (left-hand outer slice) clockwise wrt left side
const int Lf3 = 13;	//Left "face" counter-clockwise
const int Lf2 = 14;	//Left "face" 180 degrees

const int Ls  = 15;	//Left "slice" (left-hand inner slice) clockwise wrt left side
const int Ls3 = 16;	//Left "slice" counter-clockwise
const int Ls2 = 17;	//Left "slice" 180 degrees

const int Rf  = 18;	//Right "face" (right-hand outer slice) clockwise wrt right side
const int Rf3 = 19;	//Right "face" counter-clockwise
const int Rf2 = 20;	//Right "face" 180 degrees

const int Rs  = 21;	//Right "slice" (right-hand inner slice) clockwise wrt right side
const int Rs3 = 22;	//Right "slice" counter-clockwise
const int Rs2 = 23;	//Right "slice" 180 degrees

const int Ff  = 24;	//Front "face" (front outer slice) clockwise wrt front
const int Ff3 = 25;	//Front "face" counter-clockwise
const int Ff2 = 26;	//Front "face" 180 degrees

const int Fs  = 27;	//Front "slice" (front inner slice) clockwise wrt front
const int Fs3 = 28;	//Front "slice" counter-clockwise
const int Fs2 = 29;	//Front "slice" 180 degrees

const int Bf  = 30;	//Back "face" (rear outer slice) clockwise wrt back side
const int Bf3 = 31;	//Back "face" counter-clockwise
const int Bf2 = 32;	//Back "face" 180 degrees

const int Bs  = 33;	//Back "slice" (rear inner slice) clockwise wrt back side
const int Bs3 = 34;	//Back "slice" counter-clockwise
const int Bs2 = 35;	//Back "slice" 180 degrees

const int N_BASIC_MOVES  = Bs2 + 1;	//last rotate code plus one

//Twist moves (that are not also slice moves)
const int Ufs = 36;		//(Uu)
const int Ufs3 = 37;	//(Uu)'
const int Ufs2 = 38;	//(Uu)2
const int Dfs = 39;		//(Dd)
const int Dfs3 = 40;	//(Dd)'
const int Dfs2 = 41;	//(Dd)2
const int Lfs = 42;		//(Ll)
const int Lfs3 = 43;	//(Ll)'
const int Lfs2 = 44;	//(Ll)2
const int Rfs = 45;		//(Rr)
const int Rfs3 = 46;	//(Rr)'
const int Rfs2 = 47;	//(Rr)2
const int Ffs = 48;		//(Ff)
const int Ffs3 = 49;	//(Ff)'
const int Ffs2 = 50;	//(Ff)2
const int Bfs = 51;		//(Bb)
const int Bfs3 = 52;	//(Bb)'
const int Bfs2 = 53;	//(Bb)2

//Block moves (that are not also slice or twist moves)
const int UsDs3 = 54;
const int Us3Ds = 55;
const int Us2Ds2 = 56;
const int LsRs3 = 57;
const int Ls3Rs = 58;
const int Ls2Rs2 = 59;
const int FsBs3 = 60;
const int Fs3Bs = 61;
const int Fs2Bs2 = 62;

int dbltwists[27][2] = {
	{ Uf, Us }, { Uf3, Us3 }, { Uf2, Us2 },
	{ Df, Ds }, { Df3, Ds3 }, { Df2, Ds2 },
	{ Lf, Ls }, { Lf3, Ls3 }, { Lf2, Ls2 },
	{ Rf, Rs }, { Rf3, Rs3 }, { Rf2, Rs2 },
	{ Ff, Fs }, { Ff3, Fs3 }, { Ff2, Fs2 },
	{ Bf, Bs }, { Bf3, Bs3 }, { Bf2, Bs2 },
	{ Us, Ds3 }, { Us3, Ds }, { Us2, Ds2 },
	{ Ls, Rs3 }, { Ls3, Rs }, { Ls2, Rs2 },
	{ Fs, Bs3 }, { Fs3, Bs }, { Fs2, Bs2 }
};

const char* move_strings[63] = {
	"U", "U'", "U2", "u", "u'", "u2",
	"D", "D'", "D2", "d", "d'", "d2",
	"L", "L'", "L2", "l", "l'", "l2",
	"R", "R'", "R2", "r", "r'", "r2",
	"F", "F'", "F2", "f", "f'", "f2",
	"B", "B'", "B2", "b", "b'", "b2",
	"(Uu)", "(Uu)'", "(Uu)2", "(Dd)", "(Dd)'", "(Dd)2",
	"(Ll)", "(Ll)'", "(Ll)2", "(Rr)", "(Rr)'", "(Rr)2",
	"(Ff)", "(Ff)'", "(Ff)2", "(Bb)", "(Bb)'", "(Bb)2",
	"(ud')", "(u'd)", "(ud')2",
	"(lr')", "(l'r)", "(lr')2",
	"(fb')", "(f'b)", "(fb')2"
};

const int N_QTMOVES = 24;
int qt_moves[N_QTMOVES] = {
	Uf, Uf3, Us, Us3, Df, Df3, Ds, Ds3,
	Lf, Lf3, Ls, Ls3, Rf, Rf3, Rs, Rs3,
    Ff, Ff3, Fs, Fs3, Bf, Bf3, Bs, Bs3
};

const int N_SQMOVES = 12;
int sq_moves[N_SQMOVES] = { Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2 };
int sq_fc_moves[N_SQMOVES][4] = {	//Squares, fixed DRB corner moves
	{ 1, Uf2/3, 0, 0 },
	{ 1, Us2/3, 0, 0 },
	{ 1, Ds2/3, 0, 0 },
	{ 3, Uf2/3, Us2/3, Ds2/3 },
	{ 1, Lf2/3, 0, 0 },
	{ 1, Ls2/3, 0, 0 },
	{ 1, Rs2/3, 0, 0 },
	{ 3, Lf2/3, Ls2/3, Rs2/3 },
	{ 1, Ff2/3, 0, 0 },
	{ 1, Fs2/3, 0, 0 },
	{ 1, Bs2/3, 0, 0 },
	{ 3, Ff2/3, Fs2/3, Bs2/3 }
};

const int N_FACE_MOVES = 18;
int face_moves[N_FACE_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2,
    Ff, Ff3, Ff2, Bf, Bf3, Bf2
};

int basic_to_face[N_BASIC_MOVES] = {
	 0,  1,  2, -1, -1, -1,  3,  4,  5, -1, -1, -1,
	 6,  7,  8, -1, -1, -1,  9, 10, 11, -1, -1, -1,
	12, 13, 14, -1, -1, -1, 15, 16, 17, -1, -1, -1
};

const int N_SLICE_MOVES = 18;
int slice_moves[N_SLICE_MOVES] = {
	Us, Us3, Us2, Ds, Ds3, Ds2,
	Ls, Ls3, Ls2, Rs, Rs3, Rs2,
    Fs, Fs3, Fs2, Bs, Bs3, Bs2
};

int ident_table[36] = {
	 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11,
	12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
    24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
};

const int N_SQ_TWIST_MOVES = 12;
int sq_twist_moves[N_SQ_TWIST_MOVES][2] = {
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

int sq_twist_map1[N_SQ_TWIST_MOVES] = {
	Uf2, Ufs2, Dfs2, Df2,
	Lf2, Lfs2, Rfs2, Rf2,
	Ff2, Ffs2, Bfs2, Bf2
};

const int N_SQ_BLOCK_MOVES = 21;
int sq_block_moves[N_SQ_BLOCK_MOVES][3] = {
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

int sq_block_map[N_SQ_BLOCK_MOVES] = {
	Uf2, Us2, Ufs2, Dfs2, Ds2, Df2,
	Lf2, Ls2, Lfs2, Rfs2, Rs2, Rf2,
	Ff2, Fs2, Ffs2, Bfs2, Bs2, Bf2,
	Us2Ds2, Ls2Rs2, Fs2Bs2
};

const int N_MINIMAL_SQS_MOVES = 7;
int sq_minimal_sqs_moves[N_SQ_BLOCK_MOVES] = {
	Uf2/3, Us2/3, Df2/3, Lf2/3, Ls2/3, Ff2/3, Fs2/3
};

int n_moves_metric_stg5[3] = { N_SQMOVES, N_SQ_TWIST_MOVES, N_SQ_BLOCK_MOVES};

const int N_STAGE1_TWIST_MOVES_ALL = 54;
const int N_STAGE1_TWIST_MOVES = 36;
int stage1_twist_moves[N_STAGE1_TWIST_MOVES_ALL][4] = {
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

int stage1_twist_list[N_STAGE1_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
	Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
};

const int N_STAGE1_BLOCK_MOVES = 63;
const int N_STAGE1_BLOCK_MOVES_ALL = 81;
int stage1_block_moves[N_STAGE1_BLOCK_MOVES_ALL][4] = {
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

int stage1_block_list[N_STAGE1_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf, Lf3, Lf2, Ls, Ls3, Ls2, Rf, Rf3, Rf2, Rs, Rs3, Rs2,
	Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff, Ff3, Ff2, Fs, Fs3, Fs2, Bf, Bf3, Bf2, Bs, Bs3, Bs2,
	Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg1[3] = { N_BASIC_MOVES, N_STAGE1_TWIST_MOVES, N_STAGE1_BLOCK_MOVES};

const int N_STAGE2_SLICE_MOVES_OLD = 24;
int stage2_slice_moves_old[N_STAGE2_SLICE_MOVES_OLD] = {
	Uf, Uf3, Uf2, Us, Us3, Us2,
	Df, Df3, Df2, Ds, Ds3, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
};

const int N_STAGE2_SLICE_MOVES = 28;
int stage2_slice_moves[N_STAGE2_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2,
	Df, Df3, Df2, Ds, Ds3, Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
};

enum STAGE2_SLICE_LIST {
	stg2_Uf, stg2_Uf3, stg2_Uf2, stg2_Us, stg2_Us3, stg2_Us2,
	stg2_Df, stg2_Df3, stg2_Df2, stg2_Ds, stg2_Ds3, stg2_Ds2,
	stg2_Lf2, stg2_Ls, stg2_Ls3, stg2_Ls2, stg2_Rf2, stg2_Rs, stg2_Rs3, stg2_Rs2,
	stg2_Ff2, stg2_Fs, stg2_Fs3, stg2_Fs2, stg2_Bf2, stg2_Bs, stg2_Bs3, stg2_Bs2
};

const int N_STAGE2_TWIST_MOVES = 20;	//30???
int stage2_twist_moves[30 /* *0 + N_STAGE2_TWIST_MOVES */ ][4] = {
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
const int N_STAGE2_2TWIST_MOVES_OLD = 8;	// number of slice moves that can not be done by two allowed twist moves, excl L/R axis
const int N_STAGE2_2TWIST_MOVES = 16;	// number of slice moves that can not be done by two allowed twist moves
int stage2_2twist_moves[N_STAGE2_2TWIST_MOVES][3] = {
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

int stage2_twist_map1[N_STAGE2_TWIST_MOVES + N_STAGE2_2TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3,
	Lfs, Lfs3, Rfs, Rfs3, Lfs, Lfs3, Rfs, Rfs3
};

int stage2_twist_map2[N_STAGE2_TWIST_MOVES + N_STAGE2_2TWIST_MOVES] = {
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3,
	Lf3, Lf, Rf3, Rf, Lf, Lf3, Rf, Rf3
};

const int N_STAGE2_BLOCK_MOVES = 47;	//formerly 41
int stage2_block_moves[N_STAGE2_BLOCK_MOVES][4] = {
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

int stage2_block_map[N_STAGE2_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Lfs2, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg2[6] = {
	N_STAGE2_SLICE_MOVES, N_STAGE2_TWIST_MOVES, N_STAGE2_BLOCK_MOVES,
	N_STAGE2_SLICE_MOVES_OLD, N_STAGE2_TWIST_MOVES, N_STAGE2_BLOCK_MOVES
};

const int N_STAGE3_SLICE_MOVES = 20;
int stage3_slice_moves[N_STAGE3_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
};

enum STAGE3_SLICE_LIST {
	stg3_Uf, stg3_Uf3, stg3_Uf2, stg3_Us2,
	stg3_Df, stg3_Df3, stg3_Df2, stg3_Ds2,
	stg3_Lf2, stg3_Ls2, stg3_Rf2, stg3_Rs2,
	stg3_Ff2, stg3_Fs, stg3_Fs3, stg3_Fs2,
	stg3_Bf2, stg3_Bs, stg3_Bs3, stg3_Bs2
};

bool stage3_move_parity[N_STAGE3_SLICE_MOVES] = {
	false, false, false, false,
	false, false, false, false,
	false, false, false, false,
	false, true, true, false, false, true, true, false
};

const int N_STAGE3_TWIST_MOVES = 16;
int stage3_twist_moves[N_STAGE3_TWIST_MOVES][4] = {
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

const int N_STAGE3_2TWIST_MOVES_X = 4;	// number of slice moves that can not be done by two allowed twist moves
int stage3_2twist_moves_x[N_STAGE3_2TWIST_MOVES_X] = {
	stg3_Fs, stg3_Fs3, stg3_Bs, stg3_Bs3
};

const int N_STAGE3_2TWIST_MOVES = 8;	// number of slice or slice+half-turn-face moves that can not be done by two allowed twist moves
int stage3_2twist_moves[N_STAGE3_2TWIST_MOVES][2] = {
	{ stg3_Fs, -1 },
	{ stg3_Fs3, -1 },
	{ stg3_Bs, -1 },
	{ stg3_Bs3, -1 },
	{ stg3_Ff2, stg3_Fs },
	{ stg3_Ff2, stg3_Fs3 },
	{ stg3_Bf2, stg3_Bs },
	{ stg3_Bf2, stg3_Bs3 },
};

int stage3_twist_map1[N_STAGE3_TWIST_MOVES + N_STAGE3_2TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3
};

int stage3_twist_map2[N_STAGE3_TWIST_MOVES + N_STAGE3_2TWIST_MOVES] = {
	-1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3
};

const int N_STAGE3_BLOCK_MOVES = 31;
int stage3_block_moves[N_STAGE3_BLOCK_MOVES][4] = {
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

int stage3_block_map[N_STAGE3_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg3[3] = { N_STAGE3_SLICE_MOVES, N_STAGE3_TWIST_MOVES, N_STAGE3_BLOCK_MOVES};

const int N_STAGE4_SLICE_MOVES = 16;
int stage4_slice_moves[N_STAGE4_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs2, Bf2, Bs2
};

enum STAGE4_SLICE_LIST {
	stg4_Uf, stg4_Uf3, stg4_Uf2, stg4_Us2,
	stg4_Df, stg4_Df3, stg4_Df2, stg4_Ds2,
	stg4_Lf2, stg4_Ls2, stg4_Rf2, stg4_Rs2,
	stg4_Ff2, stg4_Fs2,	stg4_Bf2, stg4_Bs2
};

const int N_STAGE4_TWIST_MOVES = 16;
int stage4_twist_moves[N_STAGE3_TWIST_MOVES][4] = {
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

int stage4_twist_map1[N_STAGE4_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2
};

const int N_STAGE4_BLOCK_MOVES = 25;
int stage4_block_moves[N_STAGE4_BLOCK_MOVES][4] = {
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

int stage4_block_map[N_STAGE4_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs2, Bf2, Bs2,	Ffs2, Bfs2, Fs2Bs2
};

int n_moves_metric_stg4[3] = { N_STAGE4_SLICE_MOVES, N_STAGE4_TWIST_MOVES, N_STAGE4_BLOCK_MOVES };

const char* metric_names [3] = { "stm", "ttm", "btm" };
const char* metric_long_names[3] = { "slice", "twist", "block" };
static int sqs_perm_to_rep[24] = {
	0, 1, 2, 3, 4, 5,
	1, 0, 4, 5, 2, 3,
	3, 2, 5, 4, 0, 1,
	5, 4, 3, 2, 1, 0
};

static int sqs_rep_map[6][6] = {
	{ 0, 1, 2, 3, 4, 5 },
	{ 1, 0, 4, 5, 2, 3 },
	{ 2, 3, 0, 1, 5, 4 },
	{ 3, 2, 5, 4, 0, 1 },
	{ 4, 5, 1, 0, 3, 2 },
	{ 5, 4, 3, 2, 1, 0 }
};

static int sqs_rep_revmap[6][6] = {
	{ 0, 1, 2, 3, 4, 5 },
	{ 1, 0, 4, 5, 2, 3 },
	{ 2, 3, 0, 1, 5, 4 },
	{ 4, 5, 1, 0, 3, 2 },
	{ 3, 2, 5, 4, 0, 1 },
	{ 5, 4, 3, 2, 1, 0 }
};

static int sqs_rep_to_perm[6][4] = {
	{  0,  7, 16, 23 },
	{  1,  6, 17, 22 },
	{  2, 10, 13, 21 },
	{  3, 11, 12, 20 },
	{  4,  8, 15, 19 },
	{  5,  9, 14, 18 }
};

//map a "squares" move code to one of six "canonical" move codes,
//or -1 for moves that don't affect the corresponding pieces.
int squares_map[7][N_SQMOVES] = {
	{  0, -1,  1, -1, -1,  2, -1,  3,  4, -1,  5, -1 },		//LR edges
	{  4, -1,  5, -1,  0, -1,  1, -1, -1,  2, -1,  3 },		//FB edges
	{ -1,  2, -1,  3,  4, -1,  5, -1,  0, -1,  1, -1 },		//UD edges
	{  0, -1,  1, -1,  2, -1,  3, -1,  4, -1,  5, -1 },		//corners
	{  0, -1,  1, -1, -1,  2, -1,  3, -1,  4, -1,  5 },		//UD centers
	{ -1,  4, -1,  5,  0, -1,  1, -1, -1,  2, -1,  3 },		//LR centers
	{ -1,  2, -1,  3, -1,  4, -1,  5,  0, -1,  1, -1 }		//FB centers
};

int squares_2nd_perm[24][4];

UBYTE squares_movemap[96][6];

UBYTE squares_cen_map[12] = { 0x0F, 0x33, 0x3C, 0x55, 0x5A, 0x66, 0x99, 0xA5, 0xAA, 0xC3, 0xCC, 0xF0 };
UBYTE squares_cen_revmap[256];
UBYTE squares_cen_movemap[12][6];

int rotateEDGE_fidx[18*3] = {
	 0,  1,  0,  6,  7,  6, 12, 13, 12, 18, 19, 18, 24, 25, 24, 30, 31, 30,
	36, 37, 36, 42, 43, 42, 48, 49, 48, 54, 55, 54, 60, 61, 60, 66, 67, 66,
	72, 73, 72, 78, 79, 78, 84, 85, 84, 90, 91, 90, 96, 97, 96,102,103,102
};
int rotateEDGE_tidx[18*3] = {
	 1,  0,  2,  7,  6,  8, 13, 12, 14, 19, 18, 20, 25, 24, 26, 31, 30, 32,
    37, 36, 38, 43, 42, 44, 49, 48, 50, 55, 54, 56, 61, 60, 62, 67, 66, 68,
	73, 72, 74, 79, 78, 80, 85, 84, 86, 91, 90, 92, 97, 96, 98,103,102,104
};

int reorient_hEDGE[24] = {
	14, 12, 15, 13, 10,  8, 11,  9,  4,  6,  5,  7,
	 0,  2,  1,  3, 21, 23, 20, 22, 17, 19, 16, 18
};
int reorient_vEDGE[24] = {
	 6,  4,  7,  5,  2,  0,  3,  1, 20, 22, 21, 23,
	16, 18, 17, 19, 13, 15, 12, 14,  9, 11,  8, 10
};

int rotateEDGE_ft[18*6] = {
	 0, 12,  1, 14,  0, 12, //up face, set 1
     4,  8,  5, 10,  4,  8, //up face, set 2
	16, 22, 19, 21, 16, 22, //up slice

	 2, 15,  3, 13,  2, 15, //down face, set 1
     6, 11,  7,  9,  6, 11, //down face, set 2
    17, 23, 18, 20, 17, 23, //down slice

	 8, 20,  9, 22,  8, 20, //left face, set 1
	12, 16, 13, 18, 12, 16, //left face, set 2
	 0,  6,  3,  5,  0,  6, //left slice

	10, 23, 11, 21, 10, 23, //right face, set 1
	14, 19, 15, 17, 14, 19, //right face, set 2
	 1,  7,  2,  4,  1,  7, //right slice

	 0, 21,  2, 20,  0, 21, //front face, set 1
	 4, 17,  6, 16,  4, 17, //front face, set 2
	 8, 14, 11, 13,  8, 14, //front slice

	 1, 22,  3, 23,  1, 22, //back face, set 1
	 5, 18,  7, 19,  5, 18, //back face, set 2
	 9, 15, 10, 12,  9, 15  //back slice
};

int rotateCOR_ft[6*6] = {
	 0,  3,  2,  1,  0,  3,	//U face
	 4,  5,  6,  7,  4,  5,	//D face
     3,  0,  4,  7,  3,  0,	//L face
     1,  2,  6,  5,  1,  2,	//R face
     0,  1,  5,  4,  0,  1,	//F face
     2,  3,  7,  6,  2,  3	//B face
};

int rotateCOR_ori[4] = { 1, 2, 1, 2 };
int rotateCOR_fidx[18] = {  0,  2,  0,  6,  8,  6, 12, 14, 12, 18, 20, 18, 24, 26, 24, 30, 32, 30 };
int rotateCOR_tidx[18] = {  1,  1,  2,  7,  7,  8, 13, 13, 14, 19, 19, 20, 25, 25, 26, 31, 31, 32 };

int reorient_hCOR[8] = { 1, 2, 3, 0, 5, 6, 7, 4 };
int reorient_hCORx[24] = {  3,  4,  5,  6,  7,  8,  9, 10, 11,  0,  1,  2,
                          15, 16, 17, 18, 19, 20, 21, 22, 23, 12, 13, 14
};

int reorient_vCOR[8] = { 4, 5, 1, 0, 7, 6, 2, 3 };
int reorient_vCORx[24] = { 14, 12, 13, 16, 17, 15,  5,  3,  4,  1,  2,  0,
                          22, 23, 21, 20, 18, 19,  7,  8,  6, 11,  9, 10
};

int reorientoc_vCOR[8] = { 2, 1, 2, 1, 1, 2, 1, 2 };

int reorient_hCORSQS[8] = { 4, 5, 7, 6, 1, 0, 2, 3 };
int reorient_vCORSQS[8] = { 6, 4, 7, 5, 2, 0, 3, 1 };

#define rotateCEN_fidx	rotateEDGE_fidx
#define rotateCEN_tidx	rotateEDGE_tidx

int rotateCEN_ft[18*6] = {
	 0,  3,  1,  2,  0,  3, //up face
    16, 10, 21, 14, 16, 10, //up slice, set1
	19,  8, 22, 12, 19,  8, //up slice, set2

	 4,  7,  5,  6,  4,  7, //down face
    18, 13, 23,  9, 18, 13, //down slice, set 1
    17, 15, 20, 11, 17, 15, //down slice, set 2

	 8, 11,  9, 10,  8, 11, //left face
	16,  6, 20,  3, 16,  6, //left slice, set 1
	18,  5, 22,  0, 18,  5, //left slice, set 2

	12, 15, 13, 14, 12, 15, //right face
	19,  1, 23,  4, 19,  1, //right slice, set 1
	17,  2, 21,  7, 17,  2, //right slice, set 2

	16, 19, 17, 18, 16, 19, //front face
	 0, 14,  4, 11,  0, 14, //front slice, set 1
	 2, 13,  6,  8,  2, 13, //front slice, set 2

	20, 23, 21, 22, 20, 23, //back face
	 1, 10,  5, 15,  1, 10, //back slice, set 1
	 3,  9,  7, 12,  3,  9  //back slice, set 2
};

int reorient_hCEN[24] = {
	 2,  3,  1,  0,  7,  6,  4,  5, 19, 18, 16, 17,
	22, 23, 21, 20, 14, 15, 13, 12, 11, 10,  8,  9
};

int reorient_vCEN[24] = {
	18, 19, 17, 16, 23, 22, 20, 21, 11, 10,  8,  9,
	14, 15, 13, 12,  6,  7,  5,  4,  3,  2,  0,  1
};

int reorient_hCENSQS[24] = {
	 1,  3,  0,  2,  5,  7,  4,  6, 18, 16, 19, 17,
	22, 20, 23, 21, 12, 14, 13, 15,  8, 10,  9, 11
};

int reorient_vCENSQS[24] = {
	17, 19, 16, 18, 21, 23, 20, 22, 10,  8, 11,  9,
	14, 12, 15, 13,  4,  6,  5,  7,  0,  2,  1,  3
};

int reorient_hSCCEN[24] = {
	 4,  5,  7,  6,  1,  0,  2,  3, 21, 20, 22, 23,
	16, 17, 19, 18, 14, 15, 13, 12, 11, 10,  8,  9
};

int reorient_vSCCEN[24] = {
	20, 21, 23, 22, 17, 16, 18, 19, 13, 12, 14, 15,
	 8,  9, 11, 10,  6,  7,  5,  4,  3,  2,  0,  1
};

int mirror_rlEDGE[24] = {
	 4,  5,  6,  7,  0,  1,  2,  3, 14, 15, 12, 13,
	10, 11,  8,  9, 21, 20, 23, 22, 17, 16, 19, 18
};

int mirror_rlCENSQS[24] = {
	 1,  0,  3,  2,  5,  4,  7,  6, 12, 13, 14, 15,
	 8,  9, 10, 11, 18, 19, 16, 17, 22, 23, 20, 21
};

int mirror_rlCEN[24] = {
	 2,  3,  0,  1,  6,  7,  4,  5, 14, 15, 12, 13,
	10, 11,  8,  9, 19, 18, 17, 16, 23, 22, 21, 20
};

int mirror_rlSCCEN[24] = {
	 4,  5,  6,  7,  0,  1,  2,  3, 14, 15, 12, 13,
	10, 11,  8,  9, 21, 20, 23, 22, 17, 16, 19, 18
};

#ifdef DO_SOLVE_WHOLE_CUBE
int xlate_r6[63][6] = {
	{ 0, 24, 12,  0, 24, 12}, { 1, 25, 13,  1, 25, 13}, { 2, 26, 14,  2, 26, 14},
	{ 3, 27, 15,  3, 27, 15}, { 4, 28, 16,  4, 28, 16}, { 5, 29, 17,  5, 29, 17},
	{ 6, 30, 18,  6, 30, 18}, { 7, 31, 19,  7, 31, 19}, { 8, 32, 20,  8, 32, 20},
	{ 9, 33, 21,  9, 33, 21}, {10, 34, 22, 10, 34, 22}, {11, 35, 23, 11, 35, 23},
	{12,  0, 24, 24, 12,  0}, {13,  1, 25, 25, 13,  1}, {14,  2, 26, 26, 14,  2},
	{15,  3, 27, 27, 15,  3}, {16,  4, 28, 28, 16,  4}, {17,  5, 29, 29, 17,  5},
	{18,  6, 30, 30, 18,  6}, {19,  7, 31, 31, 19,  7}, {20,  8, 32, 32, 20,  8},
	{21,  9, 33, 33, 21,  9}, {22, 10, 34, 34, 22, 10}, {23, 11, 35, 35, 23, 11},
	{24, 12,  0, 18,  6, 30}, {25, 13,  1, 19,  7, 31}, {26, 14,  2, 20,  8, 32},
	{27, 15,  3, 21,  9, 33}, {28, 16,  4, 22, 10, 34}, {29, 17,  5, 23, 11, 35},
	{30, 18,  6, 12,  0, 24}, {31, 19,  7, 13,  1, 25}, {32, 20,  8, 14,  2, 26},
	{33, 21,  9, 15,  3, 27}, {34, 22, 10, 16,  4, 28}, {35, 23, 11, 17,  5, 29},
	{36, 48, 42, 36, 48, 42}, {37, 49, 43, 37, 49, 43}, {38, 50, 44, 38, 50, 44},
	{39, 51, 45, 39, 51, 45}, {40, 52, 46, 40, 52, 46}, {41, 53, 47, 41, 53, 47},
	{42, 36, 48, 48, 42, 36}, {43, 37, 49, 49, 43, 37}, {44, 38, 50, 50, 44, 38},
	{45, 39, 51, 51, 45, 39}, {46, 40, 52, 52, 46, 40}, {47, 41, 53, 53, 47, 41},
	{48, 42, 36, 45, 39, 51}, {49, 43, 37, 46, 40, 52}, {50, 44, 38, 47, 41, 53},
	{51, 45, 39, 42, 36, 48}, {52, 46, 40, 43, 37, 49}, {53, 47, 41, 44, 38, 50},
	{54, 60, 57, 54, 60, 57}, {55, 61, 58, 55, 61, 58}, {56, 62, 59, 56, 62, 59},
	{57, 54, 60, 60, 57, 54}, {58, 55, 61, 61, 58, 55}, {59, 56, 62, 62, 59, 56},
	{60, 57, 54, 58, 55, 61}, {61, 58, 55, 57, 54, 60}, {62, 59, 56, 59, 56, 62}
};

bool show_full_move_list = false;
bool show_per_stage_move_lists = true;
#endif

#ifdef SQUARES_INIT
#ifdef PRUNING_TABLES
UBYTE prune_table_cencor5[N_SQS_CENTER_PERM*N_SQS_CORNER_PERM/2];
UBYTE prune_table_edg5[N_SQS_EDGE_PERM/2];
UBYTE prune_table_edgcor5[N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2];
#endif
#endif

#ifdef STAGE1_INIT
USHORT reorient_co[2187][16];
UINT reorient_s1edge[N_EDGE_COMBO8][16];
#ifndef USE_SYMMETRY
UINT stage1_rep_count = 0;
#endif
#ifdef PRUNING_TABLES
UBYTE prune_table_cor1[(N_CORNER_ORIENT+1)/2];
UBYTE prune_table_edg1[(N_EDGE_COMBO8+1)/2];
#endif
#endif

#ifdef STAGE3_INIT
UINT reorient_s3cen[N_STAGE3_CENTER_CONFIGS][N_SYM_STAGE3];	//29MB
USHORT reorient_s3edge[N_STAGE3_EDGE_CONFIGS][N_SYM_STAGE3];
UINT move_table_cenSTAGE3[N_STAGE3_CENTER_CONFIGS][N_STAGE3_SLICE_MOVES];	//72MB
USHORT move_table_edgeSTAGE3[N_STAGE3_EDGE_CONFIGS][N_STAGE3_SLICE_MOVES];
USHORT stage3_edge_to_edgesym[N_STAGE3_EDGE_CONFIGS];
USHORT stage3_edgesym_to_edge[2*N_STAGE3_EDGE_CONFIGS];
#ifdef PRUNING_TABLES
UBYTE prune_table_cen3[N_STAGE3_CENTER_CONFIGS/2];
UBYTE prune_table_edg3[N_STAGE3_EDGE_CONFIGS*N_STAGE3_EDGE_PAR/2];
#endif
#endif

#ifdef STAGE4_INIT
USHORT reorient_s4cen[N_STAGE4_CENTER_CONFIGS][N_SYM_STAGE4];
UINT reorient_s4edge[N_STAGE4_EDGE_CONFIGS][N_SYM_STAGE4];
USHORT reorient_s4cor[N_STAGE4_CORNER_CONFIGS][N_SYM_STAGE4];
UINT move_table_cenSTAGE4[N_STAGE4_CENTER_CONFIGS][N_STAGE4_SLICE_MOVES];
USHORT move_table_cornerSTAGE4[N_STAGE4_CORNER_CONFIGS][N_STAGE4_SLICE_MOVES];
UINT stage4_edge88200_to_edge_sym[N_STAGE4_EDGE_CONFIGS];
UINT stage4_edge_sym_to_edge88200[N_SYM_STAGE4*N_STAGE4_EDGE_SYMCONFIGS];
USHORT stage4_edge_hB[40320];
USHORT stage4_edge_hgB[40320];
USHORT stage4_edge_hgA[40320][36];
UINT stage4_edge_hash_table_val[N_STAGE4_EDGE_HASH_TABLE];
UINT stage4_edge_hash_table_idx[N_STAGE4_EDGE_HASH_TABLE];
UINT stage4_edge_rep_table[N_STAGE4_EDGE_CONFIGS];
USHORT move_table_AedgeSTAGE4[40320][N_STAGE4_SLICE_MOVES];
USHORT move_table_BedgeSTAGE4[40320][N_STAGE4_SLICE_MOVES];
UINT stage4_solved_edge_configs[N_STAGE4_RAW_EDGE_SOLVED_CONFIGS];
int stage4repcount = 0;
USHORT stage4_solved_corner_configs[96];
#ifdef PRUNING_TABLES
UBYTE prune_table_cencor4[N_STAGE4_CORNER_CONFIGS*N_STAGE4_CENTER_CONFIGS/2];
UBYTE prune_table_edg4[N_STAGE4_EDGE_CONFIGS/2];
UBYTE prune_table_edgcen4[N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2];
#endif
#endif

const UINT STAGE3_NUM_SOLVED_CENTER_CONFIGS = 12;
UINT stage3_solved_centers[STAGE3_NUM_SOLVED_CENTER_CONFIGS] = {
	900830, 900844,	900850, 900853, 900857, 900858,
	900871, 900872, 900876,	900879, 900885, 900899
};

const UINT STAGE4_NUM_SOLVED_CENTER_CONFIGS = 12;
USHORT stage4_solved_centers_bm[STAGE4_NUM_SOLVED_CENTER_CONFIGS] = {
	0x0F, 0xF0, 0x55, 0xAA, 0x5A, 0xA5, 0x69, 0x96, 0x66, 0x99, 0x3C, 0xC3
};

#ifdef STAGE2_INIT
USHORT stage2_edge_to_edgesym[N_STAGE2_EDGE_CONFIGS];
USHORT stage2_edgesym_to_edge[N_SYM_STAGE2*N_STAGE2_EDGE_SYMCONFIGS];
USHORT reorient_cenFB[N_CENTER_COMBO4][N_SYM_STAGE2];
#ifdef PRUNING_TABLES
UBYTE prune_table_cen2[N_STAGE2_CENTER_CONFIGS/2];
UBYTE prune_table_edg2[N_STAGE2_EDGE_CONFIGS/2];
UBYTE prune_table_edgcen2[N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2];
#endif
#endif

#ifdef DO_SOLVE_STAGE3
#ifdef USE_CUBE_LIST_TABLE
UINT cube_list[N_STAGE3_TABLE_SIZE];
UINT cube_list_count = 0;
UINT cube_list_new_count = 0;
double cube_list_mult_count = 0.0;
double cube_list_new_mult_count = 0.0;
#ifndef DO_SOLVE_WHOLE_CUBE
unsigned char file_buffer[450450];
#endif
#endif
#endif

#ifdef STAGE4_INIT
bool parity_perm8_table[40320];
#endif

#ifdef PRUNING_TABLES
UINT prune_table_count = 0;
UINT prune_table_new_count = 0;

CubePruningTable* pcpt_cor1 = NULL;
CubePruningTable* pcpt_edg1 = NULL;
CubePruningTable* pcpt_cen2 = NULL;
CubePruningTable* pcpt_edg2 = NULL;
CubePruningTable* pcpt_edgcen2 = NULL;
CubePruningTable* pcpt_cen3 = NULL;
CubePruningTable* pcpt_edg3 = NULL;
CubePruningTable* pcpt_cencor4 = NULL;
CubePruningTable* pcpt_edg4 = NULL;
CubePruningTable* pcpt_edgcen4 = NULL;
CubePruningTable* pcpt_cencor5 = NULL;
CubePruningTable* pcpt_edg5 = NULL;
CubePruningTable* pcpt_edgcor5 = NULL;

void
CubePruningTableMgr::delete_cpts ()
{
	if (pcpt_cor1 != NULL) {
		delete pcpt_cor1;
	}
	if (pcpt_edg1 != NULL) {
		delete pcpt_edg1;
	}
	if (pcpt_cen2 != NULL) {
		delete pcpt_cen2;
	}
	if (pcpt_edg2 != NULL) {
		delete pcpt_edg2;
	}
	if (pcpt_edgcen2 != NULL) {
		delete pcpt_edgcen2;
	}
	if (pcpt_cen3 != NULL) {
		delete pcpt_cen3;
	}
	if (pcpt_edg3 != NULL) {
		delete pcpt_edg3;
	}
	if (pcpt_cencor4 != NULL) {
		delete pcpt_cencor4;
	}
	if (pcpt_edg4 != NULL) {
		delete pcpt_edg4;
	}
	if (pcpt_edgcen4 != NULL) {
		delete pcpt_edgcen4;
	}
	if (pcpt_cencor5 != NULL) {
		delete pcpt_cencor5;
	}
	if (pcpt_edg5 != NULL) {
		delete pcpt_edg5;
	}
	if (pcpt_edgcor5 != NULL) {
		delete pcpt_edgcor5;
	}
}
#endif

#ifdef DO_SOLVE_SQS

#ifdef USE_CUBE_LIST_TABLE
UINT cube_list[N_SQS_TABLE_SIZE];
UINT cube_list_count = 0;
UINT cube_list_new_count = 0;
#endif

#ifndef DO_SOLVE_WHOLE_CUBE
unsigned char file_buffer[256*1024];
#endif
#endif

#ifdef DO_SOLVE_STAGE1
UINT cube_list[N_STAGE1_TABLE_SIZE];
UINT cube_list_count = 0;
UINT cube_list_new_count = 0;

#ifndef DO_SOLVE_WHOLE_CUBE
//!!!!!
unsigned char file_buffer[26115];
#endif
#endif

UBYTE bm4of8[70];
UBYTE bm4of8_to_70[256];

UBYTE wi4of8_bm_to_idx[256];

#ifdef ELOC_INIT
UINT ebm2eloc[4096*4096];
UINT eloc2ebm[N_EDGE_COMBO8];
Face map96[96][8];
UINT bm12_4of8_to_high_idx[4096][70];
UINT bm12_4of8_to_low_idx[4096][70];
UBYTE bitcount8[256];
UBYTE gen_MofN8[256][256];
#endif

#ifdef CLOC_INIT
UINT c4_to_cloc[24*24*24*24];
UINT cloc_to_bm[N_CENTER_COMBO4];
#endif

#ifdef STAGE1_INIT
UINT move_table_edgeSTAGE1[N_EDGE_COMBO8][N_BASIC_MOVES];	// > 100MB !
UINT move_table_co[N_CORNER_ORIENT][N_FACE_MOVES];

#ifdef USE_SYMMETRY
double cube_list_mult_count = 0.0;
double cube_list_new_mult_count = 0.0;
#endif
#endif

#ifdef STAGE2_INIT
USHORT perm_to_420[40320];
USHORT move_table_cenSTAGE2[N_CENTER_COMBO4][N_STAGE2_SLICE_MOVES];
USHORT move_table_edgeSTAGE2[420][N_STAGE2_SLICE_MOVES];
#endif

#ifdef STAGE3_INIT
UINT e16bm2eloc[256*256];
UINT eloc2e16bm[N_COMBO_16_8];
#endif

#ifdef STAGE4_INIT
#ifndef STAGE2_INIT
USHORT perm_to_420[40320];
#endif
#endif

#ifdef DO_SOLVE_WHOLE_CUBE
unsigned char file_buffer[28000000];
FILE* stg1_file = NULL;
FILE* stg2_file = NULL;
FILE* stg3_file = NULL;
FILE* stg4_file = NULL;
FILE* stg5_file = NULL;
bool use_old_stage2 = false;
#endif

int cube_sym_inv[N_CUBESYM];

#ifdef WHOLE_CUBE
int evenodd[8] = { 0, 1, 0, 1, 1, 0, 1, 0 };	//"handedness" of corner positions
UBYTE cornerperm_to_evenoddbm[N_CORNER_PERM];  //tells which positions contain the "odd" cubies

USHORT esym_on_cornerori[N_CORNER_ORIENT][N_ESYM];
USHORT luf_on_cornerori[N_CORNER_ORIENT][N_CORNER_BM];
#endif

UINT sqs_edge_to_ep96x96x96[N_SQS_EDGE_ANTISYM_COUNT*N_ANTISYM];	//N_SQS_EDGE_SYMCOUNT*N_CUBESYM is somewhat smaller
USHORT sqs_ep96x96x96_to_edge[N_SQS_EDGE_PERM];
UBYTE sqs_ep96x96x96_to_sym[N_SQS_EDGE_PERM];

Stab sqs_edge_stab[N_SYMCOUNT];
USHORT sqs_edge_mult[N_SYMCOUNT];

UBYTE sqs_sym_cp96_table[N_SQS_CORNER_PERM][N_ANTISYM];
USHORT sqs_sym_cen_table[N_SQS_CENTER_PERM][N_ANTISYM];

struct MoveItem {
	UINT m_src_cg;
	UINT m_move_code;
	UINT m_dst_cg;
};

const int MOVE_ITEM_LIST_MAX = 280;

int display_count = 0;
int display_limit = 100;

UINT stat_dist[32];
UINT stat_dist_real[32];
UINT stat_dist_mpinv[32];
double stat_dist_full[32];

double stat_total[32];
double stat_total_real[32];
double stat_total_mpinv[32];
double stat_total_full[32];

inline int
get_dist_4bit (UINT x, const UBYTE* p)
{
	UINT x2 = x >> 1;
	UINT j = x & 0x1;
	if (j == 0) {
		return p[x2] & 0xF;
	}
	return (p[x2] >> 4) & 0xF;
}

inline void
set_dist_4bit (UINT x, UINT dist, UBYTE* p)
{
	UINT x2 = x >> 1;
	UINT j = x & 0x1;
	if (j == 0) {
		p[x2] &= 0xF0;
		p[x2] |= dist & 0xF;
		return;
	}
	p[x2] &= 0x0F;
	p[x2] |= (dist & 0xF) << 4;
}

UINT callfunc (void* pfunc, UINT idx, int move_code);
#ifdef DO_SOLVE_WHOLE_CUBE
void do_random_cubes (int metric, int count);
#endif
#ifdef SQUARES_INIT
UINT squares_move (UINT pos96, int move_code6);
UINT squares_move_corners (UINT pos96, int sqs_move_code);
UINT squares_move_edges (UINT pos96, int sqs_move_code, int edge_group);
UINT squares_move_centers (UINT pos96, int sqs_move_code, int cen_group);
UINT supersqs_move_centers (UINT pos96, int sqs_move_code, int cen_group);
void squares_unpack_centers (UINT cen1, UINT cen2, UINT cen3, Face* arr);
UINT squares_pack_centers (const Face* arr);
int multiplicitySQS (const CubeSqsCoord& cube1);
int multiplicitySCSQS (const CubeSuperSqsCoord& cube1);
void print_allsymSQS (const CubeSqsCoord& cube1);
void init_squares ();
#endif
int get_parity8 (UINT x);


#ifdef STAGE4_INIT
void init_parity_table ();
#endif
#ifdef DO_SOLVE_TEST
#ifdef DO_SOLVE_STAGE2
int get_mod_distanceSTAGE2 (const CubeStage2& cube1, int metric);
int solveitIDA_STAGE2 (const CubeStage2& init_cube, int* move_list, int metric);
bool treesearchSTAGE2 (const CubeStage2& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
#ifdef DO_SOLVE_STAGE3
int get_mod_distanceSTAGE3 (const CubeStage3& cube1, int metric);
int solveitIDA_STAGE3 (const CubeStage3& init_cube, int* move_list, int metric);
bool treesearchSTAGE3 (const CubeStage3& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
#ifdef DO_SOLVE_STAGE4
int solveitIDA_STAGE4 (const CubeStage4& init_cube, int* move_list, int metric);
bool treesearchSTAGE4 (const CubeStage4& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
#ifdef DO_SOLVE_WHOLE_CUBE
int solveit4x4x4IDA (const CubeState& init_cube, int* move_list, int metric);
void open_distance_files (int metric);
void close_distance_files ();
#endif

#else
#ifdef DO_SOLVE_STAGE4
int solveitIDA_STAGE4 (const CubeStage4& init_cube, int* move_list, int metric);
bool treesearchSTAGE4 (const CubeStage4& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
#endif

#ifdef DO_SOLVE_SQS
void loadSQS (int dist, int metric, UINT* pcube_list);
int solveitSQS (const CubeSqsCoord& init_cube, int* move_list, int metric);
int get_mod_distanceSQS (const CubeSqsCoord& cube1, int metric);
int get_distanceSQS (const CubeSqsCoord& cube1, int metric);
int solveitIDA_SQS (const CubeSqsCoord& init_cube, int* move_list, int metric);
bool treesearchSQS (const CubeSqsCoord& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
#ifdef DO_SOLVE_STAGE1
void loadSTAGE1 (int dist, int metric, UINT* pcube_list);
void loadSTAGE1_1bit_to_4bit (int dist, int metric, int fsect, UINT* pcube_list);
void writeSTAGE1_4bit (FILE* f, int fsect, UINT* pcube_list);
int get_mod_distanceSTAGE1 (const CubeStage1& cube1);
int solveitIDA_STAGE1 (const CubeStage1& init_cube, int* move_list, int metric);
bool treesearchSTAGE1 (const CubeStage1& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);
#endif
void solveitFTM (UINT cp, UINT ep, int depth, int d0, const CubeState& cube1, bool do_output);
UINT sym_on_cp96 (UINT cp96, UINT sym);
UINT sym_on_cen12x12x12 (UINT cen12x12x12, UINT sym);
UINT sym_on_eperm (UINT ep, UINT sym);
UINT sym_on_eperm_unpacked (const CubeState& cube1, UINT sym);
UINT sym_on_cperm (UINT cp, UINT sym);
#ifdef WHOLE_CUBE
void csym_calc (UINT co, UINT esym, USHORT* p_co2);
void esym_calc (UINT eo, UINT sym, USHORT* p_eo2);
bool init_edgemap ();
UINT compute_luf_on_cornerori_slow (UINT cornerori, UINT cornerperm);
UINT compute_luf_on_cornerori (UINT cornerori, UINT cornerperm);
UINT sym_on_cornerori (UINT sym, UINT co, UINT cp);
#endif
void init_4of8 ();
#ifdef ELOC_INIT
void init_eloc ();
#endif
#ifdef STAGE1_INIT
void init_stage1 ();
void init_move_tablesSTAGE1 ();
#endif
void scrambleSTAGE1 (CubeStage1* pcube, int move_count, const int* move_arr);
#ifdef CLOC_INIT
void init_cloc ();
#endif
#ifdef STAGE2_INIT
void init_stage2 ();
void stage2_cen_to_cloc4s (UINT cen, UINT *pclocf, UINT* pclocb);
UINT stage2_cen_move_fast (UINT cen, int move_code);
UINT stage2_cen_move (UINT cen, int move_code);
void scrambleSTAGE2 (CubeStage2* pcube, int move_count, const int* move_arr);
#endif
#ifdef STAGE3_INIT
void init_stage3 ();
void init_move_tablesSTAGE3 ();
void stage3_solved_center_configs ();
#endif
//#ifdef STAGE4_INIT
void array8_to_set_a (const Face* t, CubeState* result_cube);
void set_a_to_array8 (const CubeState& init_cube, Face* t);
void array8_to_set_b (const Face* t, CubeState* result_cube);
void set_b_to_array8 (const CubeState& init_cube, Face* t);
void lrfb_to_cube_state (UINT u, CubeState* result_cube);
int set_a_find96 (UINT u, UINT idx);
void set_a_check ();
void lrnum_to_cs (UINT u, CubeState* result_cube);
UINT cs_to_lrnum (const CubeState& init_cube);
UINT lr_neighbor (UINT lrnum, int mc);
int set_lr_find96 (UINT u, UINT idx);
void set_lr_check ();
bool lrfb_isrep9216_old (UINT u); //, Bitvec* bv);
UINT lrfb_find9216 (UINT u, Bitvec* bv, UINT* mylist);
UINT lrfb_get_edge_rep (UINT u);
#ifdef STAGE4_INIT
void init_stage4_edge_tables ();
void lrfb_check ();
void stage4_edge_table_init ();
bool stage4_edge_table_lookup (UINT val, UINT* hash_loc);
void add_to_stage4_edge_table (UINT val, UINT idx);
void stage4_solved_edges ();
void init_stage4 ();
void init_move_tablesSTAGE4 ();
UINT cor_find96 (UINT u, Bitvec* bv, UINT* mylist);
void stage4_cor_check ();
void check_stage4 ();
#endif
void init_edgemapSQS ();
void check_rep (const CubeSymSqsCoord& cube1);
void init_invsym ();
void rotate_sliceEDGE (int move_code, const CubeState& init_cube, CubeState* result_cube);
void rotate_sliceCORNER (int move_code, const CubeState& init_cube, CubeState* result_cube);
void convert_stage1_to_std_cube (const CubeStage1& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage1 (const CubeState& init_cube, CubeStage1* result_cube);
#ifdef STAGE2_INIT
void convert_stage2_to_std_cube (const CubeStage2& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage2 (const CubeState& init_cube, CubeStage2* result_cube);
#endif
#ifdef STAGE3_INIT
void convert_stage3_to_std_cube (const CubeStage3& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage3 (const CubeState& init_cube, CubeStage3* result_cube);
#endif
void convert_stage4_to_std_cube (const CubeStage4& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage4 (const CubeState& init_cube, CubeStage4* result_cube);
void convert_std_cube_to_squares (const CubeState& init_cube, CubeSqsCoord* result_cube);
void pack_cubeSQS (const CubeState& cube1, CubeSqsCoord* result_cube);
void unpack_cubeSQS (const CubeSqsCoord& cube1, CubeState* result_cube);
void reorient_cubeSQS (const CubeSqsCoord& init_cube, int sym, CubeSqsCoord* result_cube);
void reorient_cube_hSQS (const CubeState& init_cube, CubeState* result_cube);
void reorient_cube_vSQS (const CubeState& init_cube, CubeState* result_cube);
void mirror_cube_rlSQS (const CubeState& init_cube, CubeState* result_cube);
void inverse_cubeSQS (const CubeState& init_cube, CubeState* result_cube);
void pack_cubeSCSQS (const CubeState& cube1, CubeSuperSqsCoord* result_cube);
void unpack_cubeSCSQS (const CubeSuperSqsCoord& cube1, CubeState* result_cube);
void reorient_cubeSCSQS (const CubeSuperSqsCoord& init_cube, int sym, CubeSuperSqsCoord* result_cube);
void reorient_cube_hSCSQS (const CubeState& init_cube, CubeState* result_cube);
void reorient_cube_vSCSQS (const CubeState& init_cube, CubeState* result_cube);
void mirror_cube_rlSCSQS (const CubeState& init_cube, CubeState* result_cube);
void inverse_cubeSCSQS (const CubeState& init_cube, CubeState* result_cube);
void reorient_cubeSTAGE1_slow (const CubeStage1& init_cube, int sym, CubeStage1* result_cube);
#ifdef STAGE1_INIT
void reorient_cubeSTAGE1 (const CubeStage1& init_cube, int sym, CubeStage1* result_cube);
#endif
void reorient_cubeSTAGE2_slow (const CubeStage2& init_cube, int sym, CubeStage2* result_cube);
UINT reorient_centerFB_fast (UINT centerFB, int sym);
UINT reorient_centerFB_fast2 (UINT cloc_f, UINT cloc_b, int sym);
void get_clocFB (UINT centerFB, UINT* pcloc_f, UINT* pcloc_b);
UINT get_centerFB (UINT cloc_f, UINT cloc_b);
void reorient_cubeSTAGE3_slow (const CubeStage3& init_cube, int sym, CubeStage3* result_cube);
void reorient_cubeSTAGE3 (const CubeStage3& init_cube, int sym, CubeStage3* result_cube);
void reorient_cubeSTAGE4_slow (const CubeStage4& init_cube, int sym, CubeStage4* result_cube);
void reorient_cubeSTAGE4 (const CubeStage4& init_cube, int sym, CubeStage4* result_cube);
void reorient_cube_hCUBE (const CubeState& init_cube, CubeState* result_cube);
void reorient_cube_vCUBE (const CubeState& init_cube, CubeState* result_cube);
void mirror_cube_rlCUBE (const CubeState& init_cube, CubeState* result_cube);
void scrambleCUBE (CubeState* pcube, int move_count, const int* move_arr);
void scrambleSQS (CubeSqsCoord* pcube, int move_count, const int* move_arr);
void scrambleSCSQS (CubeSuperSqsCoord* pcube, int move_count, const int* move_arr);
UINT perm_n_pack (UINT n, const Face* array_in);
void perm_n_unpack (UINT n, UINT idx, Face* array_out);
UINT perm_n_pack2 (UINT n, UINT m, const Face* array_in);
void perm_n_unpack2 (UINT n, UINT m, UINT idx, Face* array_out);
bool get_raw_coordinates (const CubeState& cube1, UINT* cp, UINT* co, UINT* ep, UINT* eo);
bool unpack_raw_coordinates (UINT cp, UINT co, UINT ep, UINT eo, CubeState* cube1);
bool get_raw_coordinatesPERM (const CubeState& cube1, UINT* cp, UINT* ep);
bool unpack_raw_coordinatesPERM (UINT cp, UINT ep, CubeState* cube1);
void perm_n_init (int n, Face* out_arr);
void three_cycle (Face* pArr, Face f1, Face f2, Face f3);
void four_cycle (Face* pArr, Face f1, Face f2, Face f3, Face f4);
void perm_n_compose (int n, const Face* perm0_in, const Face* perm1_in, Face* perm_out);
#ifdef PRUNING_TABLES
void initGEN (UBYTE* ptable, int npositions);
void analyzeGEN (UBYTE* ptable, UINT npositions, int nmoves, int* pmove_map, int solved_positions,
				 int* psolved_list, int metric, int stage);
void generateGEN (UINT idx, int dist, int metric, UBYTE* ptable, int nmoves, int* pmove_map, int stage);
bool check_newGEN (UINT idx, UBYTE* ptable);
void add_to_cube_listGEN (UBYTE* ptable, UINT idx, int dist, int metric);
UINT do_moveGEN (UINT idx, int metric, int move_code, int stage);
UINT do_move_CEN_STAGE5 (UINT idx, int move_code);
UINT do_move_EDGE_STAGE5 (UINT idx, int sqs_move_code);
UINT do_move_CENCOR_STAGE5 (UINT idx, int sqs_move_code);
UINT do_move_EDGCOR_STAGE5 (UINT idx, int sqs_move_code);
UINT do_move_COR_STAGE1_STM (UINT idx, int move_code);
UINT do_move_EDGE_STAGE1_STM (UINT idx, int move_code);
UINT do_move_CEN_STAGE2_STM (UINT idx, int move_code);
UINT do_move_CENF_STAGE2_STM (UINT idx, int move_code);
UINT do_move_EDGE_STAGE2_STM (UINT idx, int move_code);
UINT do_move_EDGCENF_STAGE2_STM (UINT idx, int move_code);
UINT do_move_CEN_STAGE3_STM (UINT idx, int move_code);
UINT do_move_EDGE_STAGE3_STM (UINT idx, int move_code);
UINT do_move_CENCOR_STAGE4_STM (UINT idx, int move_code);
UINT do_move_EDGE_STAGE4_STM (UINT idx, int move_code);
UINT do_move_EDGCEN_STAGE4_STM (UINT idx, int move_code);
int prune_funcCOR_STAGE1 (const CubeStage1& cube1);
int prune_funcEDGE_STAGE1 (const CubeStage1& cube1);
int prune_funcCEN_STAGE2 (const CubeStage2& cube2);
int prune_funcEDGE_STAGE2 (const CubeStage2& cube2);
int prune_funcEDGCEN_STAGE2 (const CubeStage2& cube2);
int prune_funcCEN_STAGE3 (const CubeStage3& cube1);
int prune_funcEDGE_STAGE3 (const CubeStage3& cube1);
int prune_funcCENCOR_STAGE4 (const CubeStage4& cube1);
int prune_funcEDGE_STAGE4 (const CubeStage4& cube1);
int prune_funcEDGCEN_STAGE4 (const CubeStage4& cube1);
int prune_funcCENCOR_STAGE5 (const CubeSqsCoord& cube1);
int prune_funcEDGE_STAGE5 (const CubeSqsCoord& cube1);
int prune_funcEDGCOR_STAGE5 (const CubeSqsCoord& cube1);
#endif

void read_cg_1bit_file (int dist, UINT src_cg, UBYTE* ep_map);
bool read_cg_1bit_fileFTM (int dist, UINT src_cg, UBYTE* ep_map);
void check_cg_1bit_file (int dist, UINT src_cg, UBYTE* ep_map);
void read_cg_4bit_file (int d, UINT cg);
void read_cg_4bit_fileFTM (int d, UINT cg);
void load_cg_4bit_file (int d, UINT cg);
void load_cg_4bit_fileFTM (int d, UINT cg);
void read_cg_4bit_file_slow (int d, UINT cg);
void write_cg_1bit_file (int dist, UINT src_cg, UINT rel_cg, UBYTE* ep_map);
void write_cg_1bit_fileFTM (int dist, UINT src_cg, const UBYTE* ep_map);
//#endif
#ifdef DO_SOLVE_SQS
#ifdef USE_CUBE_LIST_TABLE
void write_2bit_fileSQS (int dist, int metric, UINT* pcube_list);
#endif
#endif
void write_cg_4bit_file (int dist, UINT cg, UBYTE* ep_map);
void write_cg_4bit_fileFTM (int dist, UINT cg, UBYTE* ep_map);
void read_cg_4bit_fileLM (int d, UINT cg, int rel_cg);
void read_cg_4bit_fileLM_FTM (UINT cg, int rel_cg);
void read_cg_4bit_file_as_1bitFTM (int d, int file_dist, UINT cg, UBYTE* ep_map);
void SummaryFTMQTM (int start_dist, UINT start_cgi);
#ifdef CALC_REAL_SIZE
void calculate_real_size ();
#endif
#ifdef DO_SOLVE_TEST
int get_distance (UINT cperm, UINT eperm);
#endif
int get_distanceFTM (UINT cperm, UINT eperm);
void get_representative (UINT cp, UINT ep, UINT* rep_cp, UINT* rep_ep);
bool is_representative (UINT cp, UINT ep);
bool is_representativeMPlusInv ( UINT cp, UINT ep);
void build_single_move_tables ();
void build_reorient_tables ();
void convert_to_reid_format (UINT cp, UINT co, UINT ep, UINT eo, char* buffer);
void check_cubeEP (const CubeState& cube2);
void check_cubeCP (const CubeState& cube2);
void print_cube (const CubeState& cube1);
void print_cube_ori (const CubeState& cube1);
void print_sym48 (UINT cg, UINT ep);
int get_parity_cp (UINT cp);
int get_parity_ep (UINT ep);
void disp_cycles (int n, const Face* elements);
void print_move_list (int count, const int* move_list);
void printtime ();
int countbits (UINT x);
UINT swapbits (UINT x, UINT b);
int find_next_0bit (UINT bm, int b);
int random (int n);

#ifdef USE_CUBE_LIST_TABLE
inline int
get_cube_listPACK16 (UINT idx)
{
	int i = idx / 16;
	int j = idx % 16;
	return (cube_list[i] >> (2*j)) & 0x3;
}

inline int get_cube_listPACK10 (UINT idx)
{
	int i = idx / 10;
	int j = idx % 10;
	return (cube_list[i] >> (3*j)) & 0x7;
}

inline int get_cube_listPACK12 (UINT idx)
{
	int i = idx / 12;
	int j = idx % 12;
	int b = j / 3;
	j %= 3;
	UINT x = (cube_list[i] >> (8*b)) & 0xFF;
	if (j == 0) {
		return x % 5;
	}
	if (j == 1) {
		return (x/5) % 5;
	}
	return x / 25;
}

void
update_cube_listPACK16 (UINT n)
{
	UINT i;
	const UINT N = (n + 15u)/16u;
	for (i = 0; i < N; ++i) {
		UINT x = cube_list[i];
		UINT y1 = (x ^ (x >> 1)) & 0x55555555;
		UINT y2 = (x & (x << 1)) & 0xAAAAAAAA;
		cube_list[i] = y1 | y2;
	}
}

void
update_cube_listPACK10 (UINT n)
{
	//This mapping keeps compatibility with codes of 2-bit mapping.
	// 000 (unknown) -> 000 (unknown)
	// 001 (old)     -> 001 (old)
	// 101 (2nd prev)-> 001 (old)
	// 010 (previous)-> 101 (2nd prev)
	// 011 (new)     -> 010 (previous)
	UINT i;
	const UINT N = (n + 9)/10;
	for (i = 0; i < N; ++i) {
		UINT x = cube_list[i];
		UINT y1 = (x ^ (x >> 1)) & 0x09249249;
		UINT y2 = (x & (x << 1)) & 0x12492492;
		UINT y3 = ((x << 1) & ((~x) << 2)) & 0x24924924;
		cube_list[i] = y1 | y2 | y3;
	}
}
#endif

int main (int argc, char* argv[])
{
	UINT i;
	int Xlist[12] = { Uf2, Rs2, Bf2, Fs2, Ds2, Lf2, Us2, Rf2, Ff2, Ls2, Bs2, Df2 };
#ifdef OUTPUT_DIAGNOSTICS
	printtime ();
#endif
	srand( (unsigned)time( NULL ) );
	strcpy (&datafiles_path[0], &default_datafile_path[0]);

	int start_dist = 0;
	int start_cg = 0;
	int start_cgg = 0;
	bool do_random = false;
	int random_count = 100;
	static char cmd_cubestring[300];
	bool do_cmd_cube = false;
	if (argc > 1) {
		start_dist = atoi (argv[1]);
		if (start_dist < 0 || start_dist >= 479001600) {
			printf ("bad command line arg\n");
			exit (1);
		}
	}
	bool resume = false;
	if (argc > 1) {
		start_dist = atoi (argv[1]);
		if (start_dist < 0 || start_dist > 31) {
			printf ("bad command line arg\n");
			exit (1);
		}
		if (argc > 2) {
			resume = true;
		}
	}
#ifdef DO_SOLVE_WHOLE_CUBE
	int metric = 0;
	int i2;
	for (i2 = 1; i2 < argc; ++i2) {
		if (strncmp (argv[i2], "random", 6) == 0) {
			if (strncmp (argv[i2], "random=", 7) == 0) {
				int x = atoi (&argv[i2][7]);
				if (x > 0) {
					random_count = x;
				}
			}
			if (random_count > 0) {
				do_random = true;
			}
		} else {
			if (strncmp (argv[i2], "path=", 5) == 0) {
				if (strlen (argv[i2]) > 250) {
					printf ("Path too long, ignored.\n");
				} else {
					strcpy (&datafiles_path[0], &argv[i2][5]);
				}
			} else {
				if (strncmp (argv[i2], "-", 1) == 0) {
					switch (argv[i2][1]) {
					case 's': case 'S':
						metric = 0;
						break;
					case 't': case 'T':
						metric = 1;
						break;
					case 'b': case 'B':
						metric = 2;
						break;
					}
				} else {
					strcpy (&cmd_cubestring[0], argv[i2]);
					do_cmd_cube = true;
				}
			}
		}
	}
#endif
	printf ("Performing misc. initializations...\n");
	init_4of8 ();
	init_invsym ();
#ifdef STAGE4_INIT
	init_parity_table ();
#endif
#ifdef ELOC_INIT
	init_eloc ();
#endif
#ifdef STAGE1_INIT
	printf ("Performing stage 1 initializations...\n");
	init_stage1 ();
	init_move_tablesSTAGE1 ();
#endif
#ifdef CLOC_INIT
	init_cloc ();
#endif
#ifdef STAGE2_INIT
	printf ("Performing stage 2 initializations...\n");
	init_stage2 ();
#endif

#ifdef STAGE3_INIT
	printf ("Performing stage 3 initializations...\n");
	init_stage3 ();
#endif

#ifdef STAGE4_INIT
	printf ("Performing stage 4 initializations...\n");
	stage4_solved_edges ();
	init_stage4_edge_tables ();
	lrfb_check ();
	init_stage4 ();
#endif

#ifdef SQUARES_INIT
	printf ("Performing stage 5 initializations...\n");
	init_squares ();
	init_edgemapSQS ();
	CubeSqsCoord cube5, cube5a;
	cube5.init ();
	cube5a.init ();
	for (i = 1; i < 4; ++i) {
		cube5a = cube5;
		cube5a.do_whole_cube_move (i);
	}

#endif

#ifdef PRUNING_TABLES
	cpt_mgr.init_pruning_tables (metric);
#endif

#ifdef DO_SOLVE_WHOLE_CUBE
#ifdef OUTPUT_DIAGNOSTICS
	printtime ();
#endif
	CubeStage1 solved, solved2;
	solved.init ();
	solved.m_distance = 0;
	solved2 = solved;
	solved2.do_whole_cube_move (2);
	solved2.do_whole_cube_move (1);
	solved2.do_whole_cube_move (2);
	solved2.do_whole_cube_move (1);

	do_random_cubes (metric, random_count);
	return 0;

	if (do_cmd_cube) {
		return 0;
	}
#endif

#ifdef OUTPUT_DIAGNOSTICS
	printtime ();
#endif
	return 0;
}

UINT
callfunc (void* pfunc, UINT idx, int move_code)
{
	FUNC_PTR pFunc = reinterpret_cast<FUNC_PTR>(pfunc);
	return pFunc (idx, move_code);
}

#ifdef DO_SOLVE_WHOLE_CUBE

void
do_random_cubes (int metric, int count)
{
	int i, i1, j;
	static int random_list[160];	//must be >= scramble_len
	CubeState solveme;
	CubeState solved;
	const int scramble_len = 100;	//const for now
	int success_count = 0;
	static int solveme_moves[100];

	solved.init ();

	for (i = 1; i <= count; ++i) {
		int j;
		solveme.init ();
		for (j = 0; j < scramble_len; ++j) {
			random_list[j] = random (36);
		}
		scrambleCUBE (&solveme, scramble_len, &random_list[0]);
		printf ("scramble: ");
		print_move_list (scramble_len, &random_list[0]);
		printf ("\n");
		int solveme_count = solveit4x4x4IDA (solveme, &solveme_moves[0], metric);
		if (show_full_move_list) {
			print_move_list (solveme_count, &solveme_moves[0]);
			printf ("\n");
		}
		CubeState ycube = solveme;
		scrambleCUBE (&ycube, solveme_count, &solveme_moves[0]);
		CubeState zcube = ycube;
		Face f1 = 6;
		for (i1 = 0; i1 < 6; ++i1) {
			if (zcube.m_cen[4*i1] == 0) {
				f1 = i1;
				break;
			}
		}
		switch (f1) {
		case 0:
			break;
		case 1:
			zcube.do_move (Lf2);
			zcube.do_move (Ls2);
			zcube.do_move (Rf2);
			zcube.do_move (Rs2);
			break;
		case 2:
			zcube.do_move (Ff);
			zcube.do_move (Fs);
			zcube.do_move (Bf3);
			zcube.do_move (Bs3);
			break;
		case 3:
			zcube.do_move (Ff3);
			zcube.do_move (Fs3);
			zcube.do_move (Bf);
			zcube.do_move (Bs);
			break;
		case 4:
			zcube.do_move (Lf3);
			zcube.do_move (Ls3);
			zcube.do_move (Rf);
			zcube.do_move (Rs);
			break;
		case 5:
			zcube.do_move (Lf);
			zcube.do_move (Ls);
			zcube.do_move (Rf3);
			zcube.do_move (Rs3);
			break;
		}
		switch (zcube.m_cen[8]) {
		case 2:
			break;
		case 3:
			zcube.do_move (Uf2);
			zcube.do_move (Us2);
			zcube.do_move (Df2);
			zcube.do_move (Ds2);
			break;
		case 4:
			zcube.do_move (Uf3);
			zcube.do_move (Us3);
			zcube.do_move (Df);
			zcube.do_move (Ds);
			break;
		case 5:
			zcube.do_move (Uf);
			zcube.do_move (Us);
			zcube.do_move (Df3);
			zcube.do_move (Ds3);
			break;
		}
		if (zcube.compare (solved)) {
			++success_count;
		} else {
			printf ("##### Solve error!\n");
			printf ("  "); ycube.print_super (); printf ("\n  ");
			zcube.print_super (); printf ("\n");
		}
	}
	printf ("Successful solves: %d\n", success_count);
#ifdef OUTPUT_DIAGNOSTICS
	printtime ();
#endif
}

#endif

UINT
squares_move (UINT pos96, int move_code6)
{
	if (move_code6 < 0) {
		return pos96;
	}
	return squares_movemap[pos96][move_code6];
}

UINT
squares_move_corners (UINT pos96, int sqs_move_code)
{
	return squares_move (pos96, squares_map[3][sqs_move_code]);
}

UINT
squares_move_edges (UINT pos96, int sqs_move_code, int edge_group)
{
	return squares_move (pos96, squares_map[edge_group][sqs_move_code]);
}

UINT
squares_move_centers (UINT pos12, int sqs_move_code, int cen_group)
{
	int move_code6 = squares_map[4+cen_group][sqs_move_code];
	if (move_code6 < 0) {
		return pos12;
	}
	return squares_cen_movemap[pos12][move_code6];
}

UINT
supersqs_move_centers (UINT pos96, int sqs_move_code, int cen_group)
{
	int move_code6 = squares_map[4+cen_group][sqs_move_code];
	if (move_code6 < 0) {
		return pos96;
	}
	return squares_move (pos96, move_code6);
}

void
squares_unpack_centers (UINT cen1, UINT cen2, UINT cen3, Face* arr)
{
	int i;
	UINT x = (squares_cen_map[cen1] << 16) | (squares_cen_map[cen2] << 8) | squares_cen_map[cen3];
	UINT b = 0x800000;
	for (i = 0; i < 24; ++i) {
		arr[i] = 2*(i/8) + ((x & b) == 0 ? 0 : 1);
		b >>= 1;
	}
}

UINT
squares_pack_centers (const Face* arr)
{
	int i;
	UINT x = 0;
	UINT b = 0x800000;
	for (i = 0; i < 24; ++i) {
		if ((arr[i] & 0x1) != 0) {
			x |= b;
		}
		b >>= 1;
	}
	UINT cen1 = squares_cen_revmap[(x >> 16) & 0xFF];
	UINT cen2 = squares_cen_revmap[(x >> 8) & 0xFF];
	UINT cen3 = squares_cen_revmap[x & 0xFF];
	return cen1 + 12*cen2 + 12*12*cen3;
}

void
CubeSqsCoord::init ()
{
	int i;
	m_cen12x12x12 = 0;
	m_cp96 = 0;
	m_ep96x96x96 = 0;
	m_distance = 255;
}

void
CubeSqsCoord::do_move (int sqs_move_code)
{
	UINT cen = m_cen12x12x12;
	UINT cp = m_cp96;
	UINT ep = m_ep96x96x96;
	UINT ep0 = ep%96;
	UINT ep1 = (ep/96) % 96;
	UINT ep2 = ep/(96*96);
	m_ep96x96x96 = squares_move_edges (ep0, sqs_move_code, 0) +
		96*squares_move_edges (ep1, sqs_move_code, 1) +
		96*96*squares_move_edges (ep2, sqs_move_code, 2);
	m_cp96 = squares_move_corners (cp, sqs_move_code);
	UINT cen0 = cen % 12;
	UINT cen1 = (cen/12) % 12;
	UINT cen2 = cen/(12*12); 
	m_cen12x12x12 = squares_move_centers (cen0, sqs_move_code, 0) +
		12*squares_move_centers (cen1, sqs_move_code, 1) +
		12*12*squares_move_centers (cen2, sqs_move_code, 2);
	if (! check ()) {
		printf ("do_move: inconsistent CubeSqsCoord state.\n");
		exit (1);
	}
}

void
CubeSqsCoord::do_whole_cube_move (int sqs_whole_cube_move)
{
	switch (sqs_whole_cube_move) {
	case 1:
		do_move (Uf2/3);
		do_move (Us2/3);
		do_move (Df2/3);
		do_move (Ds2/3);
		break;
	case 2:
		do_move (Ff2/3);
		do_move (Fs2/3);
		do_move (Bf2/3);
		do_move (Bs2/3);
		break;
	case 3:
		do_move (Lf2/3);
		do_move (Ls2/3);
		do_move (Rf2/3);
		do_move (Rs2/3);
		break;
	default: //case 0
		break;
	}
}

bool
CubeSqsCoord::is_representative () const
{
	bool is1 = is_representativeA ();
	bool is2 = is_representativeB ();
	if (is2 != is2) {
		printf ("is rep descrepancy\n");
	}
	return is1;
}

bool
CubeSqsCoord::is_representativeA () const
{
	if (sqs_ep96x96x96_to_sym[m_ep96x96x96] != 0) {
		return false;
	}
	UINT edge = sqs_ep96x96x96_to_edge[m_ep96x96x96];
	const Stab& stab = sqs_edge_stab[edge];
	if (stab.is_equal (stab1)) {
		return true;	//sym is 0, and no symmetry in the edges
	}
	UINT sym1;
	CubeSqsCoord cube2;
	cube2.init ();
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		if (stab.test_bit (sym1)) {
			UINT cp2 = sqs_sym_cp96_table[m_cp96][sym1];
			if (cp2 < m_cp96) {
				return false;
			}
			if (cp2 == m_cp96) {
				UINT cen2 = sqs_sym_cen_table[m_cen12x12x12][sym1];
				if (cen2 < m_cen12x12x12) {
					return false;
				}
			}
		}
	}
	return true;
}

bool
CubeSqsCoord::is_representativeB () const
{
	CubeSqsCoord rep;
	get_representative (&rep);
	return compare (rep);
}

void
CubeSqsCoord::get_representative (CubeSqsCoord* rep) const
{
	//DO IT THE LONG WAY FOR NOW
	UINT sym1;
	CubeSqsCoord cube2;
	CubeSqsCoord normcube, repcube;
	UINT my_sym = sqs_ep96x96x96_to_sym[m_ep96x96x96];
	UINT invsym = cube_sym_inv[my_sym % N_CUBESYM];
	if (my_sym >= N_CUBESYM) {
		reorient_cubeSQS (*this, N_CUBESYM, &repcube);	//invert the position first
	} else {
		repcube = *this;
	}
	UINT my_edge = sqs_ep96x96x96_to_edge[repcube.m_ep96x96x96];
	repcube.m_ep96x96x96 = normcube.m_ep96x96x96 = sqs_edge_to_ep96x96x96[N_SYMX*my_edge];
	repcube.m_cp96 = normcube.m_cp96 = sqs_sym_cp96_table[repcube.m_cp96][invsym];
	repcube.m_cen12x12x12 = normcube.m_cen12x12x12 = sqs_sym_cen_table[repcube.m_cen12x12x12][invsym];

	if (! sqs_edge_stab[my_edge].is_equal (stab1)) {
		const Stab& stab = sqs_edge_stab[my_edge];
		cube2.m_ep96x96x96 = normcube.m_ep96x96x96;
		for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
			if (stab.test_bit (sym1)) {
				cube2.m_cp96 = sqs_sym_cp96_table[normcube.m_cp96][sym1];
				cube2.m_cen12x12x12 = sqs_sym_cen_table[normcube.m_cen12x12x12][sym1];
				if (cube2.cmp (repcube) < 0) {
					repcube = cube2;
				}
			}
		}
	}
	*rep = repcube;
}

void
CubeSuperSqsCoord::init ()
{
	m_ep96x96x96 = 0;
	m_cp96 = 0;
	m_cen96x96x96 = 0;
	m_distance = 255;
}

bool
CubeSuperSqsCoord::compare (const CubeSuperSqsCoord& cube2) const
{
	return m_ep96x96x96 == cube2.m_ep96x96x96 && m_cp96 == cube2.m_cp96 &&
		m_cen96x96x96 == cube2.m_cen96x96x96;
}

int
CubeSuperSqsCoord::cmp (const CubeSuperSqsCoord& cube2) const
{
	if (m_ep96x96x96 < cube2.m_ep96x96x96) {
		return -1;
	}
	if (m_ep96x96x96 > cube2.m_ep96x96x96) {
		return 1;
	}
	if (m_cp96 < cube2.m_cp96) {
		return -1;
	}
	if (m_cp96 > cube2.m_cp96) {
		return 1;
	}
	if (m_cen96x96x96 < cube2.m_cen96x96x96) {
		return -1;
	}
	if (m_cen96x96x96 > cube2.m_cen96x96x96) {
		return 1;
	}
	return 0;
}

void
CubeSuperSqsCoord::do_move (int sqs_move_code)
{
	UINT cen = m_cen96x96x96;
	UINT cp = m_cp96;
	UINT ep = m_ep96x96x96;
	UINT ep0 = ep%96;
	UINT ep1 = (ep/96) % 96;
	UINT ep2 = ep/(96*96);
	m_ep96x96x96 = squares_move_edges (ep0, sqs_move_code, 0) +
		96*squares_move_edges (ep1, sqs_move_code, 1) +
		96*96*squares_move_edges (ep2, sqs_move_code, 2);
	m_cp96 = squares_move_corners (cp, sqs_move_code);
	UINT cen0 = cen % 96;
	UINT cen1 = (cen/96) % 96;
	UINT cen2 = cen/(96*96);
	m_cen96x96x96 = supersqs_move_centers (cen0, sqs_move_code, 0) +
		96*supersqs_move_centers (cen1, sqs_move_code, 1) +
		96*96*supersqs_move_centers (cen2, sqs_move_code, 2);
}

void
CubeSuperSqsCoord::do_whole_cube_move (int sqs_whole_cube_move)
{
	switch (sqs_whole_cube_move) {
	case 1:
		do_move (Uf2/3);
		do_move (Us2/3);
		do_move (Df2/3);
		do_move (Ds2/3);
		break;
	case 2:
		do_move (Ff2/3);
		do_move (Fs2/3);
		do_move (Bf2/3);
		do_move (Bs2/3);
		break;
	case 3:
		do_move (Lf2/3);
		do_move (Ls2/3);
		do_move (Rf2/3);
		do_move (Rs2/3);
		break;
	default: //case 0
		break;
	}
}

void
CubeSuperSqsCoord::get_representative (CubeSuperSqsCoord* rep) const
{
	//DO IT THE LONG WAY FOR NOW
	UINT sym1;
	CubeSuperSqsCoord cube2;
	CubeSuperSqsCoord lowest = *this;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		reorient_cubeSCSQS (*this, sym1, &cube2);
		if (cube2.cmp (lowest) < 0) {
			lowest = cube2;
		}
	}
	*rep = lowest;
	CubeSuperSqsCoord normcube, repcube;
	UINT my_sym = sqs_ep96x96x96_to_sym[m_ep96x96x96];
	UINT invsym = cube_sym_inv[my_sym % N_CUBESYM];
	if (my_sym >= N_CUBESYM) {
		reorient_cubeSCSQS (*this, N_CUBESYM, &repcube);	//invert the position first
	} else {
		repcube = *this;
	}
	UINT my_edge = sqs_ep96x96x96_to_edge[repcube.m_ep96x96x96];
	repcube.m_ep96x96x96 = normcube.m_ep96x96x96 = sqs_edge_to_ep96x96x96[N_SYMX*my_edge];
	repcube.m_cp96 = normcube.m_cp96 = sqs_sym_cp96_table[repcube.m_cp96][invsym];
	repcube.m_cen96x96x96 = normcube.m_cen96x96x96 = scsqs_sym_cen_table[repcube.m_cen96x96x96][invsym];

	if (! sqs_edge_stab[my_edge].is_equal (stab1)) {
		const Stab& stab = sqs_edge_stab[my_edge];
		cube2.m_ep96x96x96 = normcube.m_ep96x96x96;
		for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
			if (stab.test_bit (sym1)) {
				cube2.m_cp96 = sqs_sym_cp96_table[normcube.m_cp96][sym1];
				cube2.m_cen96x96x96 = scsqs_sym_cen_table[normcube.m_cen96x96x96][sym1];
				if (cube2.cmp (repcube) < 0) {
					repcube = cube2;
				}
			}
		}
	}
	*rep = repcube;
#endif
}

void
CubeSuperSqsCoord::print () const
{
	int j;
	CubeState cs1;
	cs1.init ();
	if (true) {
	UINT epLR = this->m_ep96x96x96 % 96;
	UINT epx = this->m_ep96x96x96 / 96;
	UINT first_perm = epLR / 4;
	UINT second_perm = squares_2nd_perm[first_perm][epLR % 4];
	perm_n_unpack (4, first_perm, &cs1.m_edge[0]);
	perm_n_unpack (4, second_perm, &cs1.m_edge[4]);
	for (j = 4; j < 8; ++j) {
		cs1.m_edge[j] += 4;
	}
	UINT epFB = epx % 96;
	UINT epUD = epx / 96;
	first_perm = epFB / 4;
	second_perm = squares_2nd_perm[first_perm][epFB % 4];
	perm_n_unpack (4, first_perm, &cs1.m_edge[8]);
	perm_n_unpack (4, second_perm, &cs1.m_edge[12]);
	for (j = 8; j < 12; ++j) {
		cs1.m_edge[j] += 8;
	}
	for (j = 12; j < 16; ++j) {
		cs1.m_edge[j] += 12;
	}
	first_perm = epUD / 4;
	second_perm = squares_2nd_perm[first_perm][epUD % 4];
	perm_n_unpack (4, first_perm, &cs1.m_edge[16]);
	perm_n_unpack (4, second_perm, &cs1.m_edge[20]);
	for (j = 16; j < 20; ++j) {
		cs1.m_edge[j] += 16;
	}
	for (j = 20; j < 24; ++j) {
		cs1.m_edge[j] += 20;
	}}
	if (true) {
	UINT cenUD = m_cen96x96x96 % 96;
	UINT cenx = m_cen96x96x96 / 96;
	UINT first_perm = cenUD / 4;
	UINT second_perm = squares_2nd_perm[first_perm][cenUD % 4];
	perm_n_unpack (4, first_perm, &cs1.m_cen[0]);
	perm_n_unpack (4, second_perm, &cs1.m_cen[4]);
	for (j = 4; j < 8; ++j) {
		cs1.m_cen[j] += 4;
	}
	UINT cenLR = cenx % 96;
	UINT cenFB = cenx / 96;
	first_perm = cenLR / 4;
	second_perm = squares_2nd_perm[first_perm][cenLR % 4];
	perm_n_unpack (4, first_perm, &cs1.m_cen[8]);
	perm_n_unpack (4, second_perm, &cs1.m_cen[12]);
	for (j = 8; j < 12; ++j) {
		cs1.m_cen[j] += 8;
	}
	for (j = 12; j < 16; ++j) {
		cs1.m_cen[j] += 12;
	}
	first_perm = cenFB / 4;
	second_perm = squares_2nd_perm[first_perm][cenFB % 4];
	perm_n_unpack (4, first_perm, &cs1.m_cen[16]);
	perm_n_unpack (4, second_perm, &cs1.m_cen[20]);
	for (j = 16; j < 20; ++j) {
		cs1.m_cen[j] += 16;
	}
	for (j = 20; j < 24; ++j) {
		cs1.m_cen[j] += 20;
	}}
	if (true) {
	UINT cp = m_cp96;
	UINT first_perm = m_cp96 / 4;
	UINT second_perm = squares_2nd_perm[first_perm][m_cp96 % 4];
	perm_n_unpack (4, first_perm, &cs1.m_cor[0]);
	perm_n_unpack (4, second_perm, &cs1.m_cor[4]);
	for (j = 4; j < 8; ++j) {
		cs1.m_cor[j] += 4;
	}}
	cs1.print_super ();
}

int
multiplicitySQS (const CubeSqsCoord& cube1)
{
	CubeSqsCoord cube2;
	cube2.init ();
	int sym1;
	int count = 1;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		reorient_cubeSQS (cube1, sym1, &cube2);
		if (cube1.compare (cube2)) {
			++count;
		}
	}
	return N_SYMX / count;
}

int
multiplicitySCSQS (const CubeSuperSqsCoord& cube1)
{
	CubeSuperSqsCoord cube2;
	cube2.init ();
	int sym1;
	int count = 1;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		reorient_cubeSCSQS (cube1, sym1, &cube2);
		if (cube1.compare (cube2)) {
			++count;
		}
	}
	int xxx = N_SYMX / count;
	return N_SYMX / count;
}

void
print_allsymSQS (const CubeSqsCoord& cube1)

{
	int sym1;
	CubeSqsCoord cube2;
	for (sym1 = 0; sym1 < N_SYMX; ++sym1) {
		reorient_cubeSQS (cube1, sym1, &cube2);
		if (sym1 == 0) {
			if (! cube1.compare (cube2)) {
				printf ("sym 0 failure!\n");
			}
		}
		printf ("sym %2d ep %6u (%5u, %2u) cp %2u cen %4u\n",
			sym1, cube2.m_ep96x96x96,
			static_cast<UINT>(sqs_ep96x96x96_to_edge[cube2.m_ep96x96x96]),
			static_cast<UINT>(sqs_ep96x96x96_to_sym[cube2.m_ep96x96x96]),
			static_cast<UINT>(cube2.m_cp96),
			static_cast<UINT>(cube2.m_cen12x12x12));
	}
	printf ("mult %2d\n\n", multiplicitySQS (cube1));
}

bool
CubeSqsCoord::is_solved () const
{
	if (m_cen12x12x12 == 0 && m_cp96 == 0 && m_ep96x96x96 == 0) {
		return true;
	}
	if (m_cen12x12x12 == 1716 && m_cp96 == 29 && m_ep96x96x96 == 881885) {
		return true;
	}
	if (m_cen12x12x12 == 143 && m_cp96 == 66 && m_ep96x96x96 == 276450) {
		return true;
	}
	if (m_cen12x12x12 == 1595 && m_cp96 == 95 && m_ep96x96x96 == 611135) {
		return true;
	}
	return false;
}

bool
CubeSqsCoord::check () const
{
	if (m_distance > 40 && m_distance != 255) {
		return false;
	}
	if (m_cen12x12x12 >= N_SQS_CENTER_PERM) {
		return false;
	}
	if (m_cp96 >= N_SQS_CORNER_PERM) {
		return false;
	}
	if (m_ep96x96x96 >= N_SQS_EDGE_PERM) {
		return false;
	}
	return true;
}

void
CubeSymSqsCoord::init ()
{
	m_ep_sym = 0;
	m_cp96 = 0;
	m_cen12x12x12 = 0;
	m_distance = 255;
}

bool
CubeSymSqsCoord::is_representative () const
{
	if (m_ep_sym % N_SYMX != 0) {
		return false;
	}
	UINT ep = m_ep_sym / N_SYMX;
	const Stab& stab = sqs_edge_stab[ep];
	if (stab.is_equal (stab1)) {
		return true;	//sym is 0, and no symmetry in the edges
	}
	UINT sym1;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		if (stab.test_bit (sym1)) {

			UINT cp2 = sqs_sym_cp96_table[m_cp96][sym1];
			if (cp2 < m_cp96) {
				return false;
			}
			if (cp2 == m_cp96) {
				UINT cen2 = sqs_sym_cen_table[m_cen12x12x12][sym1];
				if (cen2 < m_cen12x12x12) {
					return false;
				}
			}
		}
	}
	return true;
}

void
CubeSymSqsCoord::get_representative (CubeSqsCoord* rep) const
{
	//DO IT THE LONG WAY FOR NOW
	UINT sym1;
	CubeSqsCoord me, cube2;
	me.m_distance = 255;
	me.m_cen12x12x12 = m_cen12x12x12;
	me.m_cp96 = m_cp96;
	me.m_ep96x96x96 = sqs_edge_to_ep96x96x96[m_ep_sym];
	CubeSqsCoord lowest = me;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		reorient_cubeSQS (me, sym1, &cube2);
		if (cube2.cmp (lowest) < 0) {
			lowest = cube2;
		}
	}
	*rep = lowest;
}

bool
CubeSymSqsCoord::check () const
{
	if (m_distance > 40 && m_distance != 255) {
		return false;
	}
	if (m_cen12x12x12 >= 12*12*12) {
		return false;
	}
	if (m_cp96 >= 96) {
		return false;
	}
	if (m_ep_sym >= N_SYMCOUNT*N_SYMX) {
		return false;
	}
	return true;
}

void
CubeStage1::init ()
{
	m_co = 0;
	m_edge_ud_combo8 = N_EDGE_COMBO8 - 1;
	m_distance = 255;
}

bool
CubeStage1::compare (const CubeStage1& cube2) const
{
	if (m_edge_ud_combo8 != cube2.m_edge_ud_combo8) {
		return false;
	}
	return m_co == cube2.m_co;
}

int
CubeStage1::cmp (const CubeStage1& cube2) const
{
	if (m_edge_ud_combo8 > cube2.m_edge_ud_combo8) {
		return 1;
	}
	if (m_edge_ud_combo8 < cube2.m_edge_ud_combo8) {
		return -1;
	}
	if (m_co > cube2.m_co) {
		return 1;
	}
	if (m_co < cube2.m_co) {
		return -1;
	}
	return 0;
}

void
CubeStage1::do_move_slow (int move_code)
{
#ifdef ELOC_INIT
	CubeState cube1;
	convert_stage1_to_std_cube (*this, &cube1);
	cube1.do_move (move_code);
	convert_std_cube_to_stage1 (cube1, this);
#endif
}

void
CubeStage1::do_move (int move_code)
{
#ifdef STAGE1_INIT
	m_edge_ud_combo8 = move_table_edgeSTAGE1[m_edge_ud_combo8][move_code];
	int fmc = basic_to_face[move_code];
	if (fmc >= 0) {
		m_co = move_table_co[m_co][fmc];
	}
#else
	do_move_slow (move_code);
#endif
}

void
CubeStage1::do_whole_cube_move (int whole_cube_move)
{
	switch (whole_cube_move) {
	case 1:
		do_move (Uf);
		do_move (Us);
		do_move (Df3);
		do_move (Ds3);
		break;
	case 2:
		do_move (Ff);
		do_move (Fs);
		do_move (Bf3);
		do_move (Bs3);
		break;
	case 3:
		do_move (Lf);
		do_move (Ls);
		do_move (Rf3);
		do_move (Rs3);
		break;
	default: //case 0
		break;
	}
}

bool
CubeStage1::is_representative () const
{
	UINT idx = N_CORNER_ORIENT*m_edge_ud_combo8 + m_co;
	CubeStage1 cube2;
	cube2.init ();
	int sym1;
	for (sym1 = 1; sym1 < 16; ++sym1) {
#ifdef STAGE1_INIT
		reorient_cubeSTAGE1 (*this, sym1, &cube2);
#else
		reorient_cubeSTAGE1_slow (*this, sym1, &cube2);
#endif
		UINT idx2 =  N_CORNER_ORIENT*cube2.m_edge_ud_combo8 + cube2.m_co;
		if (idx2 < idx) {
			return false;
		}
	}
	return true;
}

void
CubeStage1::get_representative (CubeStage1* rep, int* mult) const
{
	UINT idx_lo = N_CORNER_ORIENT*m_edge_ud_combo8 + m_co;
	CubeStage1 cube2;
	CubeStage1 repcube = *this;
	int m = 1;
	cube2.init ();
	int sym1;
	for (sym1 = 1; sym1 < 16; ++sym1) {
#ifdef STAGE1_INIT
		reorient_cubeSTAGE1 (*this, sym1, &cube2);
#else
		reorient_cubeSTAGE1_slow (*this, sym1, &cube2);
#endif
		UINT idx2 =  N_CORNER_ORIENT*cube2.m_edge_ud_combo8 + cube2.m_co;
		if (idx2 < idx_lo) {
			repcube = cube2;
			idx_lo = idx2;
			m = 1;
		} else {
			if (idx2 == idx_lo) {
				++m;
			}
		}
	}
	*rep = repcube;
	if (mult != NULL) {
		*mult = m;
	}
}

bool
CubeStage1::is_solved () const
{
	if (m_co == 0 && m_edge_ud_combo8 == 735470) {
		return true;
	}
	if (m_co == 1373 && m_edge_ud_combo8 == 722601) {
		return true;
	}
	if (m_co == 1906 && m_edge_ud_combo8 == 0) {
		return true;
	}
	return false;
}

bool
CubeStage1::check () const
{
	if (m_co >= N_CORNER_ORIENT) {
		return false;
	}
	if (m_edge_ud_combo8 >= N_EDGE_COMBO8) {
		return false;
	}
	return true;
}

void
CubeStage2::init ()
{
	m_edge = 0;
	m_centerFB = N_STAGE2_CENTER_CONFIGS - 70;
	m_distance = 255;
}

bool
CubeStage2::compare (const CubeStage2& cube2) const
{
	if (m_edge != cube2.m_edge) {
		return false;
	}
	return m_centerFB == cube2.m_centerFB;
}

int
CubeStage2::cmp (const CubeStage2& cube2) const
{
	if (m_edge < cube2.m_edge) {
		return -1;
	}
	if (m_edge > cube2.m_edge) {
		return 1;
	}
	if (m_centerFB < cube2.m_centerFB) {
		return -1;
	}
	if (m_centerFB > cube2.m_centerFB) {
		return 1;
	}
	return 0;
}

bool
CubeStage2::is_solved () const
{
	if (m_edge == 0) {
		if (m_centerFB < 51482900 || m_centerFB > 51482969) {
			return false;
		}
		if (m_centerFB == 51482900 || m_centerFB == 51482914 || m_centerFB == 51482920 ||
			m_centerFB == 51482923 || m_centerFB == 51482927 || m_centerFB == 51482928 || 
			m_centerFB == 51482941 || m_centerFB == 51482942 || m_centerFB == 51482946 ||
			m_centerFB == 51482949 || m_centerFB == 51482955 ||  m_centerFB == 51482969)
		{
			return true;
		}
	} else {
		if (m_edge == 414) {
			if (m_centerFB < 50582070 || m_centerFB > 50582139) {
				return false;
			}
			if (m_centerFB == 50582070 || m_centerFB == 50582084 || m_centerFB == 50582090 ||
				m_centerFB == 50582093 || m_centerFB == 50582097 || m_centerFB == 50582098 ||
				m_centerFB == 50582111 || m_centerFB == 50582112 || m_centerFB == 50582116 ||
				m_centerFB == 50582119 || m_centerFB == 50582125 || m_centerFB == 50582139)
			{
				return true;
			}
		}
	}
	return false;
}

void
CubeStage2::do_move_slow (int move_code)
{
#ifdef STAGE2_INIT
	CubeState cube1;
	convert_stage2_to_std_cube (*this, &cube1);
	cube1.do_move (stage2_slice_moves[move_code]);
	convert_std_cube_to_stage2 (cube1, this);
#endif
}

void
CubeStage2::do_move (int move_code)
{
#ifdef STAGE2_INIT
	int i;
	//!!
	CubeStage2 cube2 = *this;
	Face t1[4];
	Face t2[4];
	UINT cenbm = eloc2ebm[m_centerFB / 70];
	UINT cenbm4of8 = bm4of8[m_centerFB % 70];
	int idx1a = bm12_4of8_to_high_idx[cenbm >> 12][m_centerFB % 70];
	idx1a += bm12_4of8_to_low_idx[cenbm & 0xFFF][m_centerFB % 70];
	UINT comp_70 = bm4of8_to_70[(~cenbm4of8) & 0xFF];	//could be a direct lookup
	int idx2a = bm12_4of8_to_high_idx[cenbm >> 12][comp_70];
	idx2a += bm12_4of8_to_low_idx[cenbm & 0xFFF][comp_70];
	int j1 = 0;
	int j2 = 0;
	for (i = 0; cenbm != 0; ++i) {
		if ((cenbm & 0x1) != 0) {
			if ((cenbm4of8 & 0x1) == 0) {
				t2[j2++] = i;
			} else {
				t1[j1++] = i;
			}
			cenbm4of8 >>= 1;
		}
		cenbm >>= 1;
	}
	int idx1 = 24*24*24*t1[0] + 24*24*t1[1] + 24*t1[2] + t1[3];
	int idx2 = 24*24*24*t2[0] + 24*24*t2[1] + 24*t2[2] + t2[3];
	if (idx1a != idx1) {
		printf ("do_move: idx1 mismatch (%d vs %d)\n", idx1, idx1a);
	} else {
		if (idx2a != idx2) {
			printf ("do_move: idx2 mismatch (%d vs %d)\n", idx2, idx2a);
		}
	}
	UINT cloc1 = c4_to_cloc[idx1];
	UINT cloc2 = c4_to_cloc[idx2];
	UINT cloc1b = move_table_cenSTAGE2[cloc1][move_code];
	UINT cloc2b = move_table_cenSTAGE2[cloc2][move_code];
	UINT cbm1b = cloc_to_bm[cloc1b];
	UINT cbm2b = cloc_to_bm[cloc2b];
	UINT cenbm2 = cbm1b | cbm2b;
	UINT bm48lo = gen_MofN8[cenbm2 & 0xFF][cbm1b & 0xFF];
	int bclo = bitcount8[cenbm2 & 0xFF];
	UINT bm48mid = gen_MofN8[(cenbm2 >> 8) & 0xFF][(cbm1b >> 8) & 0xFF];
	int bcmid = bitcount8[(cenbm2 >> 8) & 0xFF];
	UINT bm48hi = gen_MofN8[(cenbm2 >> 16) & 0xFF][(cbm1b >> 16) & 0xFF];
	UINT bm4of8b2 = (bm48mid << bclo) | bm48lo;
	bm4of8b2 |= (bm48hi << (bclo + bcmid));
	j1 = 0;
	UINT bm4of8b = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm2 & (1 << i)) != 0) {
			if ((cbm1b & (1 << i)) != 0) {
				bm4of8b |= (1 << j1);
			}
			++j1;
		}
	}
	if (bm4of8b2 != bm4of8b) {
		printf ("bm40f8 mismatch (%u vs. %u)\n", bm4of8b, bm4of8b2);
	}
	UINT cen1 = bm4of8_to_70[bm4of8b];
	UINT cen2 = ebm2eloc[cenbm2];
	m_centerFB = 70*cen2 + cen1;
	m_edge = move_table_edgeSTAGE2[m_edge][move_code];
#else
	do_move_slow (move_code);
#endif
}

void
CubeStage2::do_whole_cube_move (int whole_cube_move)
{
	switch (whole_cube_move) {
	case 1:	//U u d' D'
		do_move (0);
		do_move (3);
		do_move (7);
		do_move (10);
		break;
	case 2:	//F2 f2 b2 B2
		do_move (16);
		do_move (19);
		do_move (20);
		do_move (23);
		break;
	case 3:	//L2 l2 r2 R2
		do_move (12);
		do_move (13);
		do_move (14);
		do_move (15);
		break;
	default: //case 0
		break;
	}
}

bool
CubeStage2::is_representative () const
{
	bool b1 = is_representative1 ();
	bool b2 = is_representative2 ();
	if (b1 != b2) {
		printf ("is rep discrepancy!\n");
		exit (1);
	}
	return b1;
}

bool
CubeStage2::is_representative1 () const
{
#ifdef STAGE2_INIT
	int i;
	CubeStage2 cube2;
	UINT egx = stage2_edge_to_edgesym[m_edge];
	if (egx % N_SYM_STAGE2 != 0) {
		return false;
	}
	for (i = 0; i < N_SYM_STAGE2; ++i) {
		reorient_cubeSTAGE2_slow (*this, i, &cube2);
		if (cube2.cmp (*this) < 0) {
			return false;
		}
	}
#endif
	return true;
}

bool
CubeStage2::is_representative2 () const
{
#ifdef STAGE2_INIT
	int i;
	CubeStage2 cube2;
	UINT egx = stage2_edge_to_edgesym[m_edge];
	if (egx % N_SYM_STAGE2 != 0) {
		return false;
	}
	for (i = 1; i < N_SYM_STAGE2; ++i) {
		if (stage2_edgesym_to_edge[egx + i] < m_edge) {
			return false;
		}
	}
	for (i = 1; i < N_SYM_STAGE2; ++i) {
		if (stage2_edgesym_to_edge[egx + i] == m_edge) {
			reorient_cubeSTAGE2_slow (*this, i, &cube2);
			if (cube2.m_centerFB < m_centerFB) {
				return false;
			}
		}
	}
#endif
	return true;
}


void
CubeStage2::get_representative (CubeStage2* rep, int* mult) const
{
#ifdef STAGE2_INIT
	int i;
	int m = 1;
	CubeStage2 cube1;
	CubeStage2 cube2;
#ifdef OLD_SLOW
	CubeStage2 cube2x;
#endif
	CubeStage2 best = *this;
	UINT egx = stage2_edge_to_edgesym[m_edge];
	UINT sym = egx % N_SYM_STAGE2;
	UINT eg = egx / N_SYM_STAGE2;
	if (sym != 0) {
		//In this symmetry group, each element is its own inverse,
		//so sym is also inverse(sym).
#ifdef OLD_SLOW
		reorient_cubeSTAGE2_slow (*this, sym, &cube2x);
#else
		cube1.m_edge = stage2_edgesym_to_edge[N_SYM_STAGE2*eg];
		cube1.m_centerFB = reorient_centerFB_fast (m_centerFB, sym);
#endif		
	} else {
		cube1 = *this;
	}
	//The value of cube1.m_edge must be the same as
	//the edge value of the actual representative.
	best = cube1;
	bool clocs_valid = false;
	UINT cloc_f = 0;
	UINT cloc_b = 0;
	for (i = 1; i < N_SYM_STAGE2; ++i) {
		//performance optimization: only call reorient function if position has
		//the same edge coordinate value that the representative must have.
		cube2.m_edge = cube1.m_edge;
		if (stage2_edgesym_to_edge[N_SYM_STAGE2*eg + i] == cube1.m_edge) {
#ifdef OLD_SLOW
			reorient_cubeSTAGE2_slow (cube1, i, &cube2x);
#else
			if (! clocs_valid) {
				get_clocFB (cube1.m_centerFB, &cloc_f, &cloc_b);
			}
			cube2.m_centerFB = reorient_centerFB_fast2 (cloc_f, cloc_b, i);
#endif
			if (cube2.cmp (best) < 0) {
				best = cube2;
				m = 0;
			}
			if (cube2.cmp (best) == 0) {
				++m;
			}
		}
	}
	*rep = best;
	if (mult != NULL) {
		*mult = m;
	}
#endif
}

int
CubeStage2::distinct_count () const
{
#ifdef STAGE2_INIT
	int i;
	CubeStage2 cube2;
	int count = 1;
	UINT egx = stage2_edge_to_edgesym[m_edge];
	if (egx % N_SYM_STAGE2 != 0) {
		for (i = 1; i < N_SYM_STAGE2; ++i) {
			reorient_cubeSTAGE2_slow (*this, i, &cube2);
			if (compare (cube2)) {
				++count;
			}
		}
	} else {
		for (i = 1; i < N_SYM_STAGE2; ++i) {
			if (stage2_edgesym_to_edge[egx + i] == m_edge) {
				reorient_cubeSTAGE2_slow (*this, i, &cube2);
				if (cube2.m_centerFB == m_centerFB) {
					++count;
				}
			}
		}
	}
	return N_SYM_STAGE2/count;
#else
	return 0;
#endif
}

void
CubeStage2::print () const
{
#ifdef STAGE2_INIT
	int i;
	CubeState cs1;
	cs1.init ();
	printf ("(%3u, %8u) ", static_cast<UINT>(m_edge), m_centerFB);
	convert_stage2_to_std_cube (*this, &cs1);
	for (i = 0; i < 8; ++i) {
		printf (" %d", static_cast<int>(cs1.m_edge[16+i]) - 16);
	}
	for (i = 0; i < 24; ++i) {
		if (i % 8 == 0) {
			printf (" : ");
		}
		printf ("%c", "----FB"[cs1.m_cen[i]]);
	}
#endif
}

void
CubeStage3::init ()
{
	m_edge = 494;
	m_centerLR = 900830;
	m_edge_odd = false;	
	m_distance = 255;
}

bool
CubeStage3::compare (const CubeStage3& cube2) const
{
	if (m_edge_odd != cube2.m_edge_odd) {
		return false;
	}
	if (m_edge != cube2.m_edge) {
		return false;
	}
	return m_centerLR == cube2.m_centerLR;
}

int
CubeStage3::cmp (const CubeStage3& cube2) const
{
	if (m_edge_odd) {
		if (! cube2.m_edge_odd) {
			return 1;
		}
	} else {
		if (cube2.m_edge_odd) {
			return -1;
		}
	}
	if (m_edge < cube2.m_edge) {
		return -1;
	}
	if (m_edge > cube2.m_edge) {
		return 1;
	}
	if (m_centerLR < cube2.m_centerLR) {
		return -1;
	}
	if (m_centerLR > cube2.m_centerLR) {
		return 1;
	}
	return 0;
}

void
CubeStage3::do_move_slow (int move_code)
{
#ifdef STAGE3_INIT
	CubeState cube1;
	bool par = m_edge_odd;
	if (stage3_move_parity[move_code]) {
		par = ! par;
	}
	convert_stage3_to_std_cube (*this, &cube1);
	cube1.do_move (stage3_slice_moves[move_code]);
	convert_std_cube_to_stage3 (cube1, this);
	m_edge_odd = par;
#endif
}

void
CubeStage3::do_move (int move_code)
{
#ifdef STAGE3_INIT
	m_centerLR = move_table_cenSTAGE3[m_centerLR][move_code];
	m_edge = move_table_edgeSTAGE3[m_edge][move_code];
	if (stage3_move_parity[move_code]) {
		m_edge_odd = ! m_edge_odd;
	}
#else
	do_move_slow (move_code);
#endif
}

bool
CubeStage3::is_representative () const
{
	CubeStage3 s3;
	int sym;
	for (sym = 1; sym < N_SYM_STAGE3; ++sym) {
		reorient_cubeSTAGE3 (*this, sym, &s3);
		s3.m_edge_odd = m_edge_odd;		//ignore parity here
		if (cmp (s3) > 0) {
			return false;
		}
	}
	return true;
}

void
CubeStage3::get_representative (CubeStage3* rep, int* mult) const
{
	CubeStage3 rep1;
	CubeStage3 s3;
	int sym;
	rep1 = *this;
	int m = 1;
	for (sym = 1; sym < N_SYM_STAGE3; ++sym) {
		reorient_cubeSTAGE3 (*this, sym, &s3);
		s3.m_edge_odd = m_edge_odd;		//ignore parity here
		int cmp_result = s3.cmp (rep1);
		if (cmp_result < 0) {
			rep1 = s3;
			m = 1;
		} else {
			if (cmp_result == 0) {
				++m;
			}
		}
	}
	*rep = rep1;
	if (mult != NULL) {
		*mult = m;
	}
}

int
CubeStage3::distinct_count () const
{
	int i;
	CubeStage3 cube2;
	int count = 1;
	for (i = 1; i < N_SYM_STAGE3; ++i) {
		reorient_cubeSTAGE3 (*this, i, &cube2);
		cube2.m_edge_odd = m_edge_odd;	//make sure parity match, we ignore parity here
		if (compare (cube2)) {
			++count;
		}
	}
	return N_SYM_STAGE3/count;
}

bool
CubeStage3::is_solved () const
{
	int i;

	if (m_edge_odd) {
		return false;	//not solved if odd edge parity
	}
	if (m_edge != 494) {
		return false;	//not solved if wrong edge value
	}
	bool found = false;
	for (i = 0; i < STAGE3_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		if (m_centerLR == stage3_solved_centers[i]) {
			found = true;
			break;
		}
	}
	return found;	//If we found a matching center value, then it is solved.
}

void
CubeStage3::print () const
{
#ifdef STAGE3_INIT
	int i;
	CubeState cs1;
	cs1.init ();
	printf ("(%5u, %6u) ", static_cast<UINT>(m_edge), m_centerLR);
	convert_stage3_to_std_cube (*this, &cs1);
	for (i = 0; i < 16; ++i) {
		printf (" %2d", static_cast<int>(cs1.m_edge[i]));
	}
	printf (" %s", m_edge_odd ? " odd" : "even");
	for (i = 0; i < 16; ++i) {
		if (i % 8 == 0) {
			printf (" : ");
		}
		printf ("%c", "--LR--"[cs1.m_cen[i]]);
	}
#endif
}

void
CubeStage4::init ()
{
	m_edge = 0;	//?
	m_corner = 0;	//?
	m_centerUD = 0;	//?
	m_distance = 255;
}

bool
CubeStage4::compare (const CubeStage4& cube2) const
{
	if (m_edge != cube2.m_edge) {
		return false;
	}
	if (m_corner != cube2.m_corner) {
		return false;
	}
	return m_centerUD == cube2.m_centerUD;
}

int
CubeStage4::cmp (const CubeStage4& cube2) const
{
	if (m_edge < cube2.m_edge) {
		return -1;
	}
	if (m_edge > cube2.m_edge) {
		return 1;
	}
	if (m_corner < cube2.m_corner) {
		return -1;
	}
	if (m_corner > cube2.m_corner) {
		return 1;
	}
	if (m_centerUD < cube2.m_centerUD) {
		return -1;
	}
	if (m_centerUD > cube2.m_centerUD) {
		return 1;
	}
	return 0;
}

void
CubeStage4::do_move_slow (int move_code)
{
	CubeState cube1;
	convert_stage4_to_std_cube (*this, &cube1);
	cube1.do_move (stage4_slice_moves[move_code]);
	convert_std_cube_to_stage4 (cube1, this);
}

void
CubeStage4::do_move (int move_code)
{
#ifdef STAGE4_INIT
	UINT hash_idx;
	m_centerUD = move_table_cenSTAGE4[m_centerUD][move_code];
	m_corner = move_table_cornerSTAGE4[m_corner][move_code];
	UINT edge_lrfb = stage4_edge_rep_table[m_edge];
	UINT lrfbA = edge_lrfb % 40320;
	UINT lrfbB = edge_lrfb / 40320;
	UINT result_lrfb = 40320*move_table_BedgeSTAGE4[lrfbB][move_code] + move_table_AedgeSTAGE4[lrfbA][move_code];
	UINT result_edgerep = lrfb_get_edge_rep (result_lrfb);
	if (stage4_edge_table_lookup (result_edgerep, &hash_idx)) {
		m_edge = stage4_edge_hash_table_idx[hash_idx];
	} else {
		printf ("edge representative not found in hash table!\n");
		exit (1);
	}
#else
	do_move_slow (move_code);
#endif
}

bool
CubeStage4::is_representative () const
{
	CubeStage4 s4;
	int sym;
	for (sym = 1; sym < N_SYM_STAGE4; ++sym) {
		reorient_cubeSTAGE4 (*this, sym, &s4);
		if (cmp (s4) > 0) {
			return false;
		}
	}
	return true;
}

void
CubeStage4::get_representative (CubeStage4* rep, int* mult) const
{
	CubeStage4 rep1;
	CubeStage4 s4;
	int sym;
	rep1 = *this;
	int m = 1;
	for (sym = 1; sym < N_SYM_STAGE4; ++sym) {
		reorient_cubeSTAGE4 (*this, sym, &s4);
		int cmp_result = s4.cmp (rep1);
		if (cmp_result < 0) {
			rep1 = s4;
			m = 1;
		} else {
			if (cmp_result == 0) {
				++m;
			}
		}
	}
	*rep = rep1;
	if (mult != NULL) {
		*mult = m;
	}
}

int
CubeStage4::distinct_count () const
{
	int i;
	CubeStage4 cube2;
	int count = 1;
	for (i = 1; i < N_SYM_STAGE4; ++i) {
		reorient_cubeSTAGE4 (*this, i, &cube2);
		if (compare (cube2)) {
			++count;
		}
	}
	return N_SYM_STAGE4/count;
}

bool
CubeStage4::is_solved () const
{
	int i;

	if (m_corner != 0) {
		return false;	//not solved if wrong corner value
	}
	if (m_edge != 0) {
		return false;	//not solved if wrong edge value
	}
	bool found = false;
	for (i = 0; i < STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		if (m_centerUD == bm4of8_to_70[stage4_solved_centers_bm[i]]) {
			found = true;
			break;
		}
	}
	return found;	//If we found a matching center value, then it is solved.
}

void
CubeStage4::print () const
{
	int i;
	CubeState cs1;
	Face t[8];
	cs1.init ();
	UINT eA = m_edge % 40320;
	UINT eB = m_edge/40320;
	printf ("(%10u A %5u B %5u) ", m_edge, eA, eB);
	lrfb_to_cube_state (m_edge, &cs1);
	set_a_to_array8 (cs1, &t[0]);
	UINT bm = 0;
	for (i = 0; i < 8; ++i) {
		if (t[i] < 4) {
			bm |= (1 << i);
		}
	}
	UINT Abm = bm;
	UINT Alr = perm_n_pack (4, &t[0]);
	UINT Afb = perm_n_pack (4, &t[4]);
	UINT Alr6 = sqs_perm_to_rep[Alr];
	UINT Alr1 = Alr/6;
	UINT Afb6 = sqs_perm_to_rep[Afb];
	UINT Afb1 = Afb/6;
	set_b_to_array8 (cs1, &t[0]);
	bm = 0;
	for (i = 0; i < 8; ++i) {
		if (t[i] < 4) {
			bm |= (1 << i);
		}
	}
	UINT Bbm = bm;
	UINT Blr = perm_n_pack (4, &t[0]);
	UINT Bfb = perm_n_pack (4, &t[4]);
	UINT Blr6 = sqs_perm_to_rep[Blr];
	UINT Blr1 = Blr/6;
	UINT Bfb6 = sqs_perm_to_rep[Bfb];
	UINT Bfb1 = Bfb/6;
	printf ("A %3u B %3u lr (%d%c, %d%c) fb (%d%c, %d%c)",
		Abm /*4of8*/, Bbm /*B4of8*/, Alr1, "ABCDEF"[Alr6], Blr1, "ABCDEF"[Blr6],
		Afb1, "ABCDEF"[Afb6], Bfb1, "ABCDEF"[Bfb6]);
}

#ifdef SQUARES_INIT
void
init_squares ()
{
	static int mov_lst[6] = { Uf2, Df2, Ls2, Rs2, Ff2, Bf2 };
	static UBYTE cen_swapbits_map[6*2] = {
		0x90, 0x60, //Uf2
		0x09, 0x06, //Df2
		0x82, 0x28, //Ls2
		0x41, 0x14, //Rs2
		0x84, 0x48, //Fs2
		0x21, 0x12  //Bs2
	};
	UINT i, j, sym;
	CubeState cube1, cube2;
	Face t[8];
	for (i = 0; i < 24; ++i) {
		switch (sqs_perm_to_rep[i]) {
		case 0:
			squares_2nd_perm[i][0] = 0;
			squares_2nd_perm[i][1] = 7;
			squares_2nd_perm[i][2] = 16;
			squares_2nd_perm[i][3] = 23;
			break;
		case 1:
			squares_2nd_perm[i][0] = 1;
			squares_2nd_perm[i][1] = 6;
			squares_2nd_perm[i][2] = 17;
			squares_2nd_perm[i][3] = 22;
			break;
		case 2:
			squares_2nd_perm[i][0] = 2;
			squares_2nd_perm[i][1] = 10;
			squares_2nd_perm[i][2] = 13;
			squares_2nd_perm[i][3] = 21;
			break;
		case 3:
			squares_2nd_perm[i][0] = 3;
			squares_2nd_perm[i][1] = 11;
			squares_2nd_perm[i][2] = 12;
			squares_2nd_perm[i][3] = 20;
			break;
		case 4:
			squares_2nd_perm[i][0] = 4;
			squares_2nd_perm[i][1] = 8;
			squares_2nd_perm[i][2] = 15;
			squares_2nd_perm[i][3] = 19;
			break;
		case 5:
			squares_2nd_perm[i][0] = 5;
			squares_2nd_perm[i][1] = 9;
			squares_2nd_perm[i][2] = 14;
			squares_2nd_perm[i][3] = 18;
			break;
		}
	}
	cube2.init ();
	for (i = 0; i < 96; ++i) {
		cube1.init ();
		UINT first_perm = i / 4;
		UINT second_perm = squares_2nd_perm[first_perm][i % 4];
		perm_n_unpack (4, first_perm, &cube1.m_edge[0]);
		perm_n_unpack (4, second_perm, &cube1.m_edge[4]);
		for (j = 4; j < 8; ++j) {
			cube1.m_edge[j] += 4;
		}
		for (j = 0; j < 6; ++j) {
			cube2 = cube1;
			rotate_sliceEDGE (mov_lst[j], cube1, &cube2);
			UINT x1 = perm_n_pack (4, &cube2.m_edge[0]);
			UINT x2 = cube2.m_edge[4] - 4;
			if (x2 >= 4) {
				printf ("unexpected cube state\n");
				squares_movemap[i][j] = 0;
				continue;
			}
			squares_movemap[i][j] = 4*x1 + x2;
			x2 = perm_n_pack (4, &cube2.m_edge[4]);
			if (sqs_perm_to_rep[x1] != sqs_perm_to_rep[x2]) {
				printf ("perm1,perm2 inconsistency! %u %u\n", i, j);
			}
		}
	}
	for (i = 0; i < 256; ++i) {
		squares_cen_revmap[i] = 0;
	}
	for (i = 0; i < 12; ++i) {
		squares_cen_revmap[squares_cen_map[i]] = i;
	}
	for (i = 0; i < 12; ++i) {
		UINT x = squares_cen_map[i];
		for (j = 0; j < 6; ++j) {
			UINT x2 = swapbits (x, cen_swapbits_map[2*j]);
			x2 = swapbits (x2, cen_swapbits_map[2*j + 1]);
			squares_cen_movemap[i][j] = squares_cen_revmap[x2];
			if (x2 == 0) {
				printf ("Unexpected value for squares_cen_movemap[%d][%d]!\n", i, j);
			}
		}
	}
	for (i = 0; i < N_SQS_CORNER_PERM; ++i) {
		for (sym = 0; sym < N_SYMX; ++sym) {
			sqs_sym_cp96_table[i][sym] = sym_on_cp96 (i, sym);
		}
	}
	for (i = 0; i < N_SQS_CENTER_PERM; ++i) {
		for (sym = 0; sym < N_SYMX; ++sym) {
			sqs_sym_cen_table[i][sym] = sym_on_cen12x12x12 (i, sym);
		}
	}
}

#endif

int
get_parity8 (UINT x)
{
	int i, j;
	int parity = 0;
	Face t[8];
	perm_n_unpack (8, x, &t[0]);
	for (i = 0; i < 7; ++i) {
		if (t[i] == i) {
			continue;
		}
		for (j = i + 1; j < 8; ++j) {
			if (t[j] == i) {
				//"swap" the i & j elements, but don't bother updating the "i"-element
				//as it isn't needed anymore.
				t[j] = t[i];
			}
		}
		parity ^= 1;
	}
	return parity;
}

#ifdef STAGE4_INIT
void
init_parity_table ()
{
	UINT x;

	for (x = 0; x < 40320; ++x) {
		parity_perm8_table[x] = (get_parity8 (x) != 0);
	}
}
#endif

void
sort3 (UINT* arr)
{
	UINT t;
	if (arr[1] < arr[0]) {
		t = arr[0];
		arr[0] = arr[1];
		arr[1] = t;
	}
	if (arr[2] < arr[1]) {
		t = arr[1];
		arr[1] = arr[2];
		arr[2] = t;
		if (arr[1] < arr[0]) {
			t = arr[0];
			arr[0] = arr[1];
			arr[1] = t;
		}
	}
}

void
sort4 (UINT* arr)
{
	UINT t;
	if (arr[1] < arr[0]) {
		t = arr[0];
		arr[0] = arr[1];
		arr[1] = t;
	}
	if (arr[3] < arr[2]) {
		t = arr[2];
		arr[2] = arr[3];
		arr[3] = t;
	}
	if (arr[3] < arr[1]) {
		t = arr[1];
		arr[1] = arr[3];
		arr[3] = t;
	}
	if (arr[2] < arr[0]) {
		t = arr[0];
		arr[0] = arr[2];
		arr[2] = t;
	}
	if (arr[2] < arr[1]) {
		t = arr[1];
		arr[1] = arr[2];
		arr[2] = t;
	}
}

void
sort6 (UINT* arr)
{
	UINT t;
	sort3 (arr);
	sort3 (arr + 3);
	if (arr[3] < arr[0]) {
		t = arr[0];
		arr[0] = arr[3];
		arr[3] = t;
	}
	if (arr[5] < arr[2]) {
		t = arr[2];
		arr[2] = arr[5];
		arr[5] = t;
	}
	sort4 (arr + 1);
}

UINT
pack6 (UINT x)
{
	int i;
	UINT n = 0;
	UINT p = 1;
	for (i = 0; i < 6;  ++i) {
		n += p*(x & 0xF);
		x >>= 4;
		p *= 12;
	}
	return n;
}

#ifdef DO_SOLVE_SQS
void
loadSQS (int dist, int metric, UINT* pcube_list)
{
	UINT i;
	UINT j, k;
	const int N_FILE_SIZE = N_SQS/8;
	const int N_BUFFER_SIZE = N_SQS_CORNER_PERM*N_SQS_CENTER_PERM/8;
	const int N_BUFFERS = N_FILE_SIZE/N_BUFFER_SIZE;
	char fname[64];
	TableIndex ti;
	sprintf (&fname[0], "%ssquares_%s_new_%02d.rbk", &datafiles_path[0], metric_names[metric], dist);
	FILE* f = NULL;

	f = fopen (&fname[0], "rb");
	if (f == NULL) {
		printf ("could not open '%s'\n", &fname[0]);
		exit (1);
	}

#ifdef USE_TITLE_BAR
	if (true) {
		char str[48];
		sprintf (&str[0], "title reading 1bit file %u", dist);
		system (&str[0]);
	}
#endif
	for (i = 0; i < N_BUFFERS; ++i) {
		int n = fread (&file_buffer[0], 1, N_BUFFER_SIZE, f);
		if (n != N_BUFFER_SIZE) {
			printf ("file read error in '%s'.\n", &fname[0]);
		}
		for (j = 0; j < N_BUFFER_SIZE; ++j) {
			UBYTE u = file_buffer[j];
			for (k = 0; k < 8; ++k) {
				if (dist == 1 && ((u & 0x1) != 0)) {
					printf ("dist %d (%u,%u,%u) (%u, %u)\n", dist, i, j, k, (8*j + k)/1728, (8*j + k) % 1728);
				}
				if ((u & 0x1) != 0) {
					UINT idx = 8*N_BUFFER_SIZE*i + 8*j + k;
					ti.init (idx);
					ti.set_value (pcube_list, dist & 0x3);
				}
				u >>= 1;
			}
			file_buffer[j] = u;
		}
	}
	fclose (f);
}

int
solveitSQS (const CubeSqsCoord& init_cube, int* move_list, int metric)
{
	int i;
	int j, jx4;
	CubeSqsCoord cube1 = init_cube;
	CubeSqsCoord cube2, cube3;
	cube2.init ();
	cube3.init ();
	int n_moves = n_moves_metric_stg5[metric];
	int count = 0;
	bool go_again = true;
	int dist = get_mod_distanceSQS (cube1, metric);
	while (go_again) {
		int dist2 = dist;
		int dist3 = dist;
		bool found = false;
		for (i = 0; i < n_moves; ++i) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (i);
				break;
			case 1:
				cube2.do_move (sq_twist_moves[i][0]);
				if (sq_twist_moves[i][1] >= 0) {
					cube2.do_move (sq_twist_moves[i][1]);
				}
				break;
			case 2:
				cube2.do_move (sq_block_moves[i][0]);
				if (sq_block_moves[i][1] >= 0) {
					cube2.do_move (sq_block_moves[i][1]);
				}
				break;
			}
			dist2 = get_mod_distanceSQS (cube2, metric);
			if (dist2 == ((dist - 1) & 0x3)) {
				if (! found) {
					//cube1 = cube2;  // commented out because we don't break below
					cube3 = cube2;	//save the value for later
					dist3 = dist2;
					if (move_list == NULL) {
						++count;
					} else {
						switch (metric) {
						case 0:
							move_list[count++] = i;		//3*i + 2 !!!!
							break;
						case 1:
							move_list[count++] = sq_twist_map1[i];
							break;
						case 2:
							move_list[count++] = sq_block_map[i];
							break;
						}
					}
					found = true;
				}
				//break;	//do break for better performance, less consistency checking
			}
			if (dist2 == ((dist + 2) & 0x3)) {
				printf ("difference of 2 mod 4 error\n");
				return -1;
			}
		}
		if (found) {
			cube1 = cube3;
			dist = dist3;
		} else {
			go_again = false;
		}
	}
	if (count > 19) {
		printf ("large move count %d\n", count);
	}
	return count;
}

int
get_mod_distanceSQS (const CubeSqsCoord& cube1, int metric)
{
	CubeSqsCoord normcube;
	const int fdist_metric[7] = { 19, 20, 16, 0, 0, 0, 16 };
	cube1.get_representative (&normcube);
	UINT my_edge = sqs_ep96x96x96_to_edge[normcube.m_ep96x96x96];

	UINT idx_edge = N_SQS_CORNER_PERM*N_SQS_CENTER_PERM*my_edge;
	UINT idx = idx_edge + N_SQS_CENTER_PERM*normcube.m_cp96 + normcube.m_cen12x12x12;
#ifdef USE_CUBE_LIST_TABLE
	TableIndex ti;
	ti.init (idx);
	return ti.get_value (&cube_list[0]);
#else
	int x;
	char fname[64];
	FILE* stg5_file = NULL;	//!!!
	FILE* f = NULL;
	FILE* fx = NULL;
	int fdist = fdist_metric[metric];
	if (stg5_file == NULL) {
		sprintf (&fname[0], "%ssquares_%s_distm4_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist);

		f = fopen (&fname[0], "rb");
		if (f == NULL) {
			printf ("could not open '%s'\n", &fname[0]);
			exit (1);
		}
		fx = f;
	} else {
		fx = stg5_file;
	}
	UINT idx4 = idx/4u;
	int idxj = idx & 0x3;
	int result = fseek (fx, static_cast<long>(idx4), SEEK_SET);
	if (result != 0) {
		UINT i;
		for (i = 0; i < idx4; ++i) {
			x = getc (fx);
			if (x == EOF) {
				printf ("Unexpected EOF in file\n");
				return 31;
			}
		}
	}
	x = getc (fx);
	if (x == EOF) {
		printf ("Unexpected EOF in file\n");
	}
	if (f != NULL) {
		fclose (f);
	}
	return (x >> (2*idxj)) & 0x3;
#endif
}

int
get_distanceSQS (const CubeSqsCoord& cube1, int metric)
{
	CubeSqsCoord normcube;
	const int fdist_metric[6] = { 19, 20, 16, 0, 0, 0 };
	if (metric != 2) {
		return 31;
	}
	cube1.get_representative (&normcube);
	UINT my_edge = sqs_ep96x96x96_to_edge[normcube.m_ep96x96x96];
	UINT idx_edge = N_SQS_CORNER_PERM*N_SQS_CENTER_PERM*my_edge;
	UINT idx = idx_edge + N_SQS_CENTER_PERM*normcube.m_cp96 + normcube.m_cen12x12x12;
	int x, d;
	char fname[64];
	FILE* fx = NULL;
	int fdist = fdist_metric[metric];
	UINT idx8 = idx/8u;
	int idxj = idx & 0x7;
	int count = 0;
	int dd = -1;
	for (d = fdist; d >= 0; --d) {
		sprintf (&fname[0], "%ssquares_%s_new_%02d.rbk", &datafiles_path[0], metric_names[metric], d);

		fx = fopen (&fname[0], "rb");
		if (fx == NULL) {
			printf ("could not open '%s'\n", &fname[0]);
			exit (1);
		}

		int result = fseek (fx, static_cast<long>(idx8), SEEK_SET);
		if (result != 0) {
			UINT i;
			for (i = 0; i < idx8; ++i) {
				x = getc (fx);
				if (x == EOF) {
					printf ("Unexpected EOF in file\n");
					return 31;
				}
			}
		}
		x = getc (fx);
		if (x == EOF) {
			printf ("Unexpected EOF in file\n");
		}
		fclose (fx);
		if ( ((x >> idxj) & 0x1) != 0) {
			dd = d;
			++count;
		}
	}
	if (count == 0) {
		printf ("Could not find distance it bitvector files\n");
		return -1;
	}
	if (count > 1) {
		printf ("duplicate-distance position, count = %d\n", count);
	}
	return dd;
}

#ifdef PRUNING_TABLES
int
solveitIDA_SQS (const CubeSqsCoord& init_cube, int* move_list, int metric)
{
	static UINT init_move_state[3] = { 12, 23, 6 };
	int move_count;
	int g1;
	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSQS (init_cube, g1, 0, init_move_state[metric], g1, metric,
				move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

UINT sqs_slice_moves_to_try [13] = {
	0xFFE, 0xFFC, 0xFF8, 0xFF0,
	0xFEF, 0xFCF, 0xF8F, 0xF0F,
	0xEFF, 0xCFF, 0x8FF, 0x0FF,
	0xFFF
};

UINT sqs_stm_next_ms[N_SQMOVES] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

const UINT SQS_TW_MS_U = 0;
const UINT SQS_TW_MS_D = 1;
const UINT SQS_TW_MS_Uu = 2;
const UINT SQS_TW_MS_u = 3;
const UINT SQS_TW_MS_d = 4;
const UINT SQS_TW_MS_UD = 5;
const UINT SQS_TW_MS_Ud = 6;

const UINT SQS_TW_MS_L = 8;
const UINT SQS_TW_MS_R = 9;
const UINT SQS_TW_MS_Ll = 10;
const UINT SQS_TW_MS_l = 11;
const UINT SQS_TW_MS_r = 12;
const UINT SQS_TW_MS_LR = 13;
const UINT SQS_TW_MS_Lr = 14;

const UINT SQS_TW_MS_F = 16;
const UINT SQS_TW_MS_B = 17;
const UINT SQS_TW_MS_Ff = 18;
const UINT SQS_TW_MS_f = 19;
const UINT SQS_TW_MS_b = 20;
const UINT SQS_TW_MS_FB = 21;
const UINT SQS_TW_MS_Fb = 22;

const UINT SQS_TW_MS_X = 23;

UINT sqs_twist_moves_to_try[24] = {
	0xBBA, 0xBB4, 0xBB0, 0xBB0, 0xBB0, 0xBB4, 0xBB0, 0xBB0,
	0xBAB, 0xB4B, 0xB0B, 0xB0B, 0xB0B, 0xB4B, 0xB0B, 0xB0B,
	0xABB, 0x4BB, 0x0BB, 0x0BB, 0x0BB, 0x4BB, 0x0BB, 0xBBB
};

#define	SQST_XX			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X
#define	SQST_XU			SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D
#define	SQST_XL			SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R
#define	SQST_XF			SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B
#define SQST_U0			SQS_TW_MS_X,SQS_TW_MS_u,SQS_TW_MS_X,SQS_TW_MS_UD
#define SQST_U1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_d,SQS_TW_MS_X
#define SQST_U5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Ud,SQS_TW_MS_X
#define SQST_L0			SQS_TW_MS_X,SQS_TW_MS_l,SQS_TW_MS_X,SQS_TW_MS_LR
#define SQST_L1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_r,SQS_TW_MS_X
#define SQST_L5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Lr,SQS_TW_MS_X
#define SQST_F0			SQS_TW_MS_X,SQS_TW_MS_f,SQS_TW_MS_X,SQS_TW_MS_FB
#define SQST_F1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_b,SQS_TW_MS_X
#define SQST_F5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Fb,SQS_TW_MS_X

UINT sqs_twist_next_ms[24][12] = {
	{ SQST_U0, SQST_XL, SQST_XF },
	{ SQST_U1, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_U5, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XX, SQST_XX },

	{ SQST_XU, SQST_L0, SQST_XF },
	{ SQST_XU, SQST_L1, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_L5, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XX, SQST_XX, SQST_XX },

	{ SQST_XU, SQST_XL, SQST_F0 },
	{ SQST_XU, SQST_XL, SQST_F1 },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_F5 },
	{ SQST_XU, SQST_XL, SQST_XF },

	{ SQST_XU, SQST_XL, SQST_XF }
};

const UINT SQS_BL_MS_U = 0;
const UINT SQS_BL_MS_XU = 1;
const UINT SQS_BL_MS_L = 2;
const UINT SQS_BL_MS_XL = 3;
const UINT SQS_BL_MS_F = 4;
const UINT SQS_BL_MS_XF = 5;
const UINT SQS_BL_MS_X = 6;

#define	SQSB_XX			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X
#define	SQSB_XU			SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU
#define SQSB_U0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_X
#define	SQSB_XL			SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL
#define SQSB_L0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_X
#define	SQSB_XF			SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF
#define SQSB_F0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_X

UINT sqs_block_moves_to_try[7] = {
	0x1B7DD0, 0x1B7DC0, 0x177437, 0x177037, 0x0D0DF7, 0x0C0DF7, 0x1F7DF7
};

UINT sqs_block_next_ms[7][21] = {
	{ SQSB_U0, SQSB_XL, SQSB_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQSB_XX, SQSB_XL, SQSB_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_L0, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_XX, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_XL, SQSB_F0, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQSB_XU, SQSB_XL, SQSB_XX, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQSB_XU, SQSB_XL, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_XF }
};
#endif

#ifdef PRUNING_TABLES
bool
treesearchSQS (const CubeSqsCoord& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeSqsCoord cube2;
	int mov_idx, mc, j;
	UINT next_ms = 0;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			if (got_interrupt) {
				throw 1;
			}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = prune_funcEDGCOR_STAGE5 (cube1);
	if (dist <= depth) {
		dist = prune_funcCENCOR_STAGE5 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg5[metric]; ++mov_idx) {
			bool did_move = false;
			cube2 = cube1;
			switch (metric) {
			case 0:
				if ((sqs_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2.do_move (mov_idx);
					next_ms = sqs_stm_next_ms[mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((sqs_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; j < 2 && sq_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = sqs_twist_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 2:
				if ((sqs_block_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; sq_block_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_block_moves[mov_idx][j];
						cube2.do_move (mc);
					}
					next_ms = sqs_block_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			}
			if (did_move) {
				mc = mov_idx;
				switch (metric) {
				case 1:
					mc = sq_twist_map1[mov_idx];
					break;
				case 2:
					mc = sq_block_map[mov_idx];
					break;
				}
				move_list[moves_done] = mc;
				if (treesearchSQS (cube2, depth - 1, moves_done + 1,
						next_ms, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
	}
	return false;
}
#endif
#endif

#ifdef DO_SOLVE_STAGE1
void
loadSTAGE1 (int dist, int metric, UINT* pcube_list)
{
	UINT i;
	UINT j, k;
	const int N_FILE_SIZE = (N_STAGE1+7)/8;
	const int N_BUFFER_SIZE = 26115;
	const int N_BUFFERS = N_FILE_SIZE/N_BUFFER_SIZE;
	char fname[64];
	TableIndex ti;
	sprintf (&fname[0], "%sstage1_%s_new_%02d.rbk", &datafiles_path[0], metric_names[metric], dist);
	FILE* f = NULL;

	f = fopen (&fname[0], "rb");
	if (f == NULL) {
		printf ("could not open '%s'\n", &fname[0]);
		exit (1);
	}

#ifdef USE_TITLE_BAR
	if (true) {
		char str[64];
		sprintf (&str[0], "title reading stage1 1bit file %u", dist);
		system (&str[0]);
	}
#endif
	for (i = 0; i < N_BUFFERS; ++i) {
		int n = fread (&file_buffer[0], 1, N_BUFFER_SIZE, f);
		if (n != N_BUFFER_SIZE) {
			printf ("file read error in '%s'.\n", &fname[0]);
		}
		for (j = 0; j < N_BUFFER_SIZE; ++j) {
			UBYTE u = file_buffer[j];
			for (k = 0; k < 8; ++k) {
				if ((u & 0x1) != 0) {
					UINT idx = 8*N_BUFFER_SIZE*i + 8*j + k;
					ti.init (idx);
					ti.set_value (pcube_list, dist & 0x3);
				}
				u >>= 1;
			}
		}
	}
	fclose (f);
}

void
loadSTAGE1_1bit_to_4bit (int dist, int metric, int fsect, UINT* pcube_list)
{
	UINT i;
	UINT j, k;
	const UINT N_FILE_SIZE = (N_STAGE1+7)/8;
	const UINT N_BUFFER_SIZE = 26115;
	const UINT N_BUFFERS = N_FILE_SIZE/N_BUFFER_SIZE;
	const UINT N_BUFFERS_SECT = N_BUFFERS/2;	//remainder is 1
	const UINT N_ELTS_LAST_BUFFER_SECT1 = 13058*8;
	const UINT N_ELTS_LAST_BUFFER_SECT2 = N_STAGE1 - (8u*2u*N_BUFFER_SIZE*N_BUFFERS_SECT) - N_ELTS_LAST_BUFFER_SECT1;	//13058*8 - 11;

	char fname[64];
	TableIndex ti;
	sprintf (&fname[0], "H:\\Revenge\\stage1_%s_new_%02d.rbk", metric_names[metric], dist);
	FILE* f = NULL;

	f = fopen (&fname[0], "rb");
	if (f == NULL) {
		printf ("could not open '%s'\n", &fname[0]);
		exit (1);
	}

#ifdef USE_TITLE_BAR
	if (true) {
		char str[64];
		sprintf (&str[0], "title reading stage1 1bit file %u", dist);
		system (&str[0]);
	}
#endif
	if (fsect == 1) {
		int result = fseek (f, N_BUFFERS_SECT*N_BUFFER_SIZE + N_ELTS_LAST_BUFFER_SECT1/8, SEEK_SET);
		if (result != 0) {
			printf ("Seek error in file '%s'.\n", &fname[0]);
		}
	}
	for (i = 0; i <= N_BUFFERS_SECT; ++i) {	// Use "<=" because we have extra partial buffer
		int nread = N_BUFFER_SIZE;
		if (i == N_BUFFERS_SECT) {
			memset (&file_buffer[0], 0, N_BUFFER_SIZE);
			if (fsect == 1) {
				nread = (N_ELTS_LAST_BUFFER_SECT2 + 7)/8;
			} else {
				nread = N_ELTS_LAST_BUFFER_SECT1/8;
			}
		}
		int n = fread (&file_buffer[0], 1, nread, f);
		if (n != nread) {
			printf ("file read error in '%s'.\n", &fname[0]);
		}
		for (j = 0; j < static_cast<UINT>(nread); ++j) {
			UBYTE u = file_buffer[j];
			UINT idx = N_BUFFER_SIZE*i + j;
			for (k = 0; k < 8; ++k) {
				if ((u & 0x1) != 0) {
					pcube_list[idx] &= ~(0xF << (4*k));
					pcube_list[idx] |= (dist << (4*k));
				}
				u >>= 1;
			}
		}
	}
	fclose (f);
}

void
writeSTAGE1_4bit (FILE* f, int fsect, UINT* pcube_list)
{
	UINT i;
	UINT j1, j, k;
	const UINT N_READ_FILE_SIZE = (N_STAGE1+7)/8;
	const UINT N_READ_BUFFER_SIZE = 26115;
	const UINT N_READ_BUFFERS = N_READ_FILE_SIZE/N_READ_BUFFER_SIZE;
	const UINT N_READ_BUFFERS_SECT = N_READ_BUFFERS/2;	//remainder is 1
	const UINT N_ELTS_LAST_READ_BUFFER_SECT1 = 13058*8;
	const UINT N_ELTS_LAST_READ_BUFFER_SECT2 = N_STAGE1
		- (8u*2u*N_READ_BUFFER_SIZE*N_READ_BUFFERS_SECT)
		- N_ELTS_LAST_READ_BUFFER_SECT1;	//13058*8 - 11;

	const UINT NBYTES_SECT1 = (8*N_READ_BUFFER_SIZE*N_READ_BUFFERS_SECT + N_ELTS_LAST_READ_BUFFER_SECT1)/2;
	const UINT NBYTES_SECT2 = (8*N_READ_BUFFER_SIZE*N_READ_BUFFERS_SECT + N_ELTS_LAST_READ_BUFFER_SECT2 + 1)/2;
	const UINT N_BUFFER_SIZE = 24000;
	const UINT N_BUFFERS = (NBYTES_SECT1) / N_BUFFER_SIZE;
	const UINT NBYTES_LAST_BUFFER_SECT1 = NBYTES_SECT1 - N_BUFFER_SIZE*N_BUFFERS;
	const UINT NBYTES_LAST_BUFFER_SECT2 = NBYTES_SECT2 - N_BUFFER_SIZE*N_BUFFERS;

#ifdef USE_TITLE_BAR
	if (true) {
		char str[64];
		sprintf (&str[0], "title writing stage1 4bit file %d", fsect);
		system (&str[0]);
	}
#endif
	for (i = 0; i <= N_BUFFERS; ++i) {	// Use "<=" because we have extra partial buffer
		int nwrite = static_cast<int>(N_BUFFER_SIZE);
		if (i == N_BUFFERS) {
			if (fsect == 1) {
				nwrite = static_cast<int>(NBYTES_LAST_BUFFER_SECT2);
			} else {
				nwrite = static_cast<int>(NBYTES_LAST_BUFFER_SECT1);
			}
		}
		for (j1 = 0; j1 < static_cast<UINT>(nwrite); ++j1) {
			j = j1 / 4;
			k = j1 % 4;
			UINT idx = (N_BUFFER_SIZE/4)*i + j;
			file_buffer[j1] = (pcube_list[idx] >> (8*k)) & 0xFF;
		}
		int n = fwrite (&file_buffer[0], 1, nwrite, f);
		if (n != nwrite) {
			printf ("file write error in dist file.\n");
			return;
		}
	}
}

#ifdef PRUNING_TABLES
int
solveitIDA_STAGE1 (const CubeStage1& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE1 (init_cube, g1, 0, 4, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE1 (const CubeStage1& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage1 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			if (got_interrupt) {
				throw 1;
			}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = prune_funcCOR_STAGE1 (cube1);
	if (dist <= depth) {
		dist = prune_funcEDGE_STAGE1 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg1[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				for (j = 0; stage1_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				for (j = 0; stage1_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			move_list[moves_done] = mov_idx;
			if (treesearchSTAGE1 (cube2, depth - 1, moves_done + 1,
						0, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}
#endif
#endif

#ifdef DO_SOLVE_STAGE2

int
get_mod_distanceSTAGE2 (const CubeStage2& cube1, int metric)
{
	int x;
	CubeStage2 repcube;
	char fname[64];
	const int stg2_fdist[7] = { 16, 18, 15, 0, 0, 0, 11 };		//old twist result, max dist was 19
	const int stg2_fdist_old[7] = { 18, 18, 15, 0, 0, 11 }; 
	cube1.get_representative (&repcube, NULL);
	UINT edge = stage2_edge_to_edgesym[repcube.m_edge]/N_SYM_STAGE2;
	UINT center = repcube.m_centerFB;
	double idx;
	if (use_old_stage2 && metric == 0) {
		idx = static_cast<double>(N_STAGE2_CENTER_CONFIGS) * static_cast<double>(edge) +
			static_cast<double>(center);
	} else {
		idx = static_cast<double>(N_STAGE2_CENTER_CONFIGS + 6) * static_cast<double>(edge) +
			static_cast<double>(center);
	}
	FILE* f = NULL;
	FILE* fx = NULL;
	int fdist = stg2_fdist[metric];
	const char* dist_str = (metric == 1 ? "distm6" : "distm4");
	if (stg2_file == NULL) {
		sprintf (&fname[0], "%sstage2_%s_%s_%02d.rbk", &datafiles_path[0], metric_names[metric], dist_str, fdist);

		f = fopen (&fname[0], "rb");
		if (f == NULL) {
			printf ("could not open '%s'\n", &fname[0]);
			exit (1);
		}
		fx = f;
	} else {
		fx = stg2_file;
	}
	UINT idx4;
	int idxj;
	if (metric == 1) {
		idx4 = static_cast<UINT>(idx/3.0);
		idxj = static_cast<int>(idx - 3.0 * static_cast<double>(idx4));		//idx modulo 3
	} else {
		idx4 = static_cast<UINT>(idx/4.0);
		idxj = static_cast<int>(idx - 4.0 * static_cast<double>(idx4));		//idx modulo 4
	}
	int result = fseek (fx, static_cast<long>(idx4), SEEK_SET);
	if (result != 0) {
		UINT i;
		for (i = 0; i < idx4; ++i) {
			x = getc (fx);
			if (x == EOF) {
				printf ("Unexpected EOF in file\n");
				return 31;
			}
		}
	}
	x = getc (fx);
	if (x == EOF) {
		printf ("Unexpected EOF in file\n");
	}
	if (f != NULL) {
		fclose (f);
	}
	if (metric == 1) {
		if (idxj == 0) {
			return x % 6;
		}
		if (idxj == 1) {
			return (x / 6) % 6;
		}
		return x / 36;
	}
	return (x >> (2*idxj)) & 0x3;
}

#ifdef PRUNING_TABLES

const UINT STG2_SL_MS_X = 0;
const UINT STG2_SL_MS_U = 1;
const UINT STG2_SL_MS_u = 2;
const UINT STG2_SL_MS_d = 3;
const UINT STG2_SL_MS_D = 4;
const UINT STG2_SL_MS_L = 5;
const UINT STG2_SL_MS_l = 6;
const UINT STG2_SL_MS_r = 7;
const UINT STG2_SL_MS_R = 8;
const UINT STG2_SL_MS_F = 9;
const UINT STG2_SL_MS_f = 10;
const UINT STG2_SL_MS_b = 11;
const UINT STG2_SL_MS_B = 12;

const UINT STG2_TW_MS_X = 0;
const UINT STG2_TW_MS_u = 1;
const UINT STG2_TW_MS_U = 2;
const UINT STG2_TW_MS_d = 3;
const UINT STG2_TW_MS_D = 4;
const UINT STG2_TW_MS_l = 5;
const UINT STG2_TW_MS_L = 6;
const UINT STG2_TW_MS_r = 7;
const UINT STG2_TW_MS_R = 8;
const UINT STG2_TW_MS_f = 9;
const UINT STG2_TW_MS_F = 10;
const UINT STG2_TW_MS_b = 11;
const UINT STG2_TW_MS_B = 12;

#define	STG2S_X		STG2_SL_MS_X
#define	STG2S_Xx3	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X
#define	STG2S_U0x3	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U
#define STG2S_U1x3	STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u
#define	STG2S_U2x3	STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d
#define	STG2S_U3x3	STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D
#define	STG2S_L0	STG2_SL_MS_L
#define	STG2S_L1x3	STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l
#define	STG2S_L1x3_OLD	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l
#define	STG2S_L2x3	STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r
#define	STG2S_L3	STG2_SL_MS_R
#define STG2S_F0	STG2_SL_MS_F
#define STG2S_F1x3	STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f
#define STG2S_F2x3	STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b
#define	STG2S_F3	STG2_SL_MS_B

#define	STG2T_Xx3	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_Xx4	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_U0x3	STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u
#define STG2T_U1x3	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U
#define	STG2T_D0x3	STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d
#define	STG2T_D1x3	STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D
#define	STG2T_LRlr	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r
#define	STG2T_LRXr	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r
#define	STG2T_XRXr	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r
#define	STG2T_XRXX	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_FBfb	STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b
#define	STG2T_FBXb	STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b
#define	STG2T_XBXb	STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b
#define	STG2T_XBXX	STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_LRFB	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_XRFB	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_XXFB	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_LRXB	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_B
#define	STG2T_LRXX	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X

UINT stage2_slice_moves_to_try_old [13] = {
	0xFF99FFF,
	0xFF99FF8, 0xFF99FC0, 0xFF991C0, 0xFF99000,
	0xFF98FFF, 0xFF90FFF, 0x0000000, 0x0000000,
	0xFE99FFF, 0xF099FFF, 0x1099FFF, 0x0099FFF
};

UINT stage2_slice_moves_to_try [13] = {
	0xFFFFFFF,
	0xFFFFFF8, 0xFFFFFC0, 0xFFFF1C0, 0xFFFF000,
	0xFFFEFFF, 0xFFF0FFF, 0xFF10FFF, 0xFF00FFF,
	0xFEFFFFF, 0xF0FFFFF, 0x10FFFFF, 0x00FFFFF
};

UINT stage2_stm_next_ms_old[13][N_STAGE2_SLICE_MOVES] = {
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_Xx3,STG2S_L0,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_L0,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_L1x3_OLD,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },

	{	STG2S_X,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3 },
	{	STG2S_X,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3 },

	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_Xx3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3 }
};

UINT stage2_stm_next_ms[13][N_STAGE2_SLICE_MOVES] = {
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_Xx3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_L3,STG2S_Xx3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_Xx3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3 }
};

UINT stage2_2twist_types[N_STAGE2_2TWIST_MOVES] = {
	22, 22, 23, 23, 22, 22, 23, 23,
	20, 20, 21, 21, 20, 20, 21, 21
};

UINT stage2_twist_moves_to_try [13] = {
	0xFFFFFF,
	0xFFFE3F, 0xFFFE38, 0xFFF038, 0xFFF000,
	0xEFBFFF, 0xEFAFFF, 0xCF2FFF, 0xCF0FFF,
	0xBBFFFF, 0xBAFFFF, 0x32FFFF, 0x30FFFF
};

UINT stage2_twist_next_ms[13][24] = {
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_Xx3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_D1x3,STG2T_Xx3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_D1x3,STG2T_Xx3,STG2T_Xx3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_Xx3,STG2T_Xx3,STG2T_Xx3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRXr,STG2T_FBfb,STG2T_XRFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_XRXr,STG2T_FBfb,STG2T_XRFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_XRXX,STG2T_FBfb,STG2T_XXFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_Xx4,STG2T_FBfb,STG2T_XXFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_FBXb,STG2T_LRXB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_XBXb,STG2T_LRXB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_XBXX,STG2T_LRXX },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_Xx4,STG2T_LRXX }
};

const int S2BMTT_X = 0;		//moves not used for IDA* search
const int S2BMTT_UGEN = 1;	//U-axis general moves
const int S2BMTT_u = 2;
const int S2BMTT_u3 = 3;
const int S2BMTT_u2 = 4;
const int S2BMTT_D = 5;
const int S2BMTT_D3 = 6;
const int S2BMTT_D2 = 7;
const int S2BMTT_d = 8;
const int S2BMTT_d3 = 9;
const int S2BMTT_d2 = 10;
const int S2BMTT_u2d2 = 11;
const int S2BMTT_ud3 = 12;	//and u3d
const int S2BMTT_LGEN = 13;	//L-Axis general moves
const int S2BMTT_l = 14;	//and l'
const int S2BMTT_r = 15;
const int S2BMTT_r3 = 16;
const int S2BMTT_r2 = 17;
const int S2BMTT_lr3 = 18;	//and l'r
const int S2BMTT_FGEN = 19;	//F-Axis general moves
const int S2BMTT_f = 20;	//and f'
const int S2BMTT_b = 21;
const int S2BMTT_b3 = 22;
const int S2BMTT_b2 = 23;
const int S2BMTT_fb3 = 24;	//and f'b

UBYTE stage2_btm_mtt_idx[N_STAGE2_BLOCK_MOVES] = {
	S2BMTT_UGEN, S2BMTT_UGEN, S2BMTT_UGEN,		//U, U', U2
	S2BMTT_u, S2BMTT_u3, S2BMTT_u2,				//u, u', u2
	S2BMTT_D, S2BMTT_D3, S2BMTT_D2,				//D, D', D2
	S2BMTT_d, S2BMTT_d3, S2BMTT_d2,				//d, d', d2
	S2BMTT_UGEN, S2BMTT_UGEN, S2BMTT_UGEN,		//(Uu), (Uu)', (Uu)2
	S2BMTT_X, S2BMTT_X, S2BMTT_X,				//(Dd), (Dd)', (Dd)2
	S2BMTT_ud3, S2BMTT_ud3, S2BMTT_u2d2,		//(ud'), (u'd), (ud')2
	S2BMTT_LGEN,								//L2
	S2BMTT_l, S2BMTT_l, S2BMTT_LGEN,			//l, l', l2
	S2BMTT_LGEN,								//R2
	S2BMTT_r, S2BMTT_r3, S2BMTT_r2,				//r, r', r2
	S2BMTT_LGEN, S2BMTT_X,						//(Ll)2, (Rr)2
	S2BMTT_lr3, S2BMTT_lr3, S2BMTT_LGEN,		//(lr'), (l'r), (lr')2
	S2BMTT_FGEN,								//F2
	S2BMTT_f, S2BMTT_f, S2BMTT_FGEN,			//f, f', f2
	S2BMTT_FGEN,								//B2
	S2BMTT_b, S2BMTT_b3, S2BMTT_b2,				//b, b', b2
	S2BMTT_FGEN, S2BMTT_X,						//(Ff)2, (Bb)2
	S2BMTT_fb3, S2BMTT_fb3, S2BMTT_FGEN			//(fb'), (f'b), (fb')2
};

#define S2BMTT_MK_U_ALL (0xFFF << S2BMTT_UGEN)
#define S2BMTT_MK_L_ALL (0x3F << S2BMTT_LGEN)
#define S2BMTT_MK_F_ALL (0x3F << S2BMTT_FGEN)
#define S2BMTT_MK_ALL_d (1 << S2BMTT_d) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)
#define S2BMTT_MK_U1 ((1 << S2BMTT_u3) | (1 << S2BMTT_u2) | (1 << S2BMTT_D) | (1 << S2BMTT_D2) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_U3 ((1 << S2BMTT_u) | (1 << S2BMTT_u2) | (1 << S2BMTT_D3) | (1 << S2BMTT_D2) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_U2 ((1 << S2BMTT_u) | (1 << S2BMTT_u3) | (1 << S2BMTT_D) | (1 << S2BMTT_D3) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_u1 ((1 << S2BMTT_D) | (1 << S2BMTT_D2) | (1 << S2BMTT_d) | (1 << S2BMTT_d2))
#define S2BMTT_MK_u3 ((1 << S2BMTT_D3) | (1 << S2BMTT_D2) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2))
#define S2BMTT_MK_u2 ((1 << S2BMTT_D) | (1 << S2BMTT_D3) | (1 << S2BMTT_d) | (1 << S2BMTT_d3))
#define S2BMTT_MK_D1 ((1 << S2BMTT_d3) | (1 << S2BMTT_d2))
#define S2BMTT_MK_D3 ((1 << S2BMTT_d) | (1 << S2BMTT_d2))
#define S2BMTT_MK_D2 ((1 << S2BMTT_d) | (1 << S2BMTT_d3))
#define S2BMTT_MK_Uu (1 << S2BMTT_ud3)
#define S2BMTT_MK_xll3 (1 << S2BMTT_l)
#define S2BMTT_MK_xrr3 ((1 << S2BMTT_r) | (1 << S2BMTT_r3))
#define S2BMTT_MK_xlr3 (1 << S2BMTT_lr3)
#define S2BMTT_MK_L2 (S2BMTT_MK_xll3 | S2BMTT_MK_xrr3 | (1 << S2BMTT_r2) | (S2BMTT_MK_xlr3))
#define S2BMTT_MK_R2 (S2BMTT_MK_xll3 | S2BMTT_MK_xrr3)
#define S2BMTT_MK_l ((1 << S2BMTT_r) | (1 << S2BMTT_r2))
#define S2BMTT_MK_l3 ((1 << S2BMTT_r3) | (1 << S2BMTT_r2))
#define S2BMTT_MK_xff3 (1 << S2BMTT_f)
#define S2BMTT_MK_xbb3 ((1 << S2BMTT_b) | (1 << S2BMTT_b3))
#define S2BMTT_MK_xfb3 (1 << S2BMTT_fb3)
#define S2BMTT_MK_F2 (S2BMTT_MK_xff3 | S2BMTT_MK_xbb3 | (1 << S2BMTT_b2) | (S2BMTT_MK_xfb3))
#define S2BMTT_MK_B2 (S2BMTT_MK_xff3 | S2BMTT_MK_xbb3)
#define S2BMTT_MK_f ((1 << S2BMTT_b) | (1 << S2BMTT_b2))
#define S2BMTT_MK_f3 ((1 << S2BMTT_b3) | (1 << S2BMTT_b2))

UINT stage2_block_moves_to_try [29] = {
	S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_u2d2) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_ud3) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_D3) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_D) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_L2 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_R2 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_l | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_l3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_xrr3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_xlr3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_F2 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_B2 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_f | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_f3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_xbb3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_xfb3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL
};

#define	STG2B_U_ANY	1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14
#define	STG2B_U_GEN	14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14
#define	STG2B_U2	14,14,14,13,12,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14
#define	STG2B_L_ANY	15,17,18,19,16,21,21,21,20,0,21,21,21
#define STG2B_L_GEN	21,21,21,21,21,21,21,21,21,0,21,21,21
#define	STG2B_F_ANY	22,24,25,26,23,28,28,28,27,0,28,28,28
#define STG2B_F_GEN	28,28,28,28,28,28,28,28,28,0,28,28,28

UINT stage2_btm_next_ms[29][47] = {
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U2, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN }
};

int
solveitIDA_STAGE2 (const CubeStage2& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE2 (init_cube, g1, 0, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE2 (const CubeStage2& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage2 cube2;
	int mov_idx, mc, j;
	UINT next_ms = 0;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			if (got_interrupt) {
				throw 1;
			}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0; //prune_funcCEN_STAGE2 (cube1);
	if (dist <= depth) {
		dist = prune_funcEDGCEN_STAGE2 (cube1);
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < N_STAGE2_2TWIST_MOVES; ++mov_idx) {
				int mtype = stage2_2twist_types[mov_idx];
				if ((stage2_twist_moves_to_try[move_state] & (1 << mtype)) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_2twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_2twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_twist_next_ms[move_state][mtype];
					move_list[moves_done] = stage2_twist_map1[N_STAGE2_TWIST_MOVES + mov_idx];
					move_list[moves_done + 1] = stage2_twist_map2[N_STAGE2_TWIST_MOVES + mov_idx];
					if (treesearchSTAGE2 (cube2, depth - 2, moves_done + 2,
							next_ms, goal, metric, move_list, pmove_count))
					{
						return true;
					}
				}
			}
		}
		int xmetric = metric;
		if (use_old_stage2) {
			xmetric += 3;
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg2[xmetric]; ++mov_idx) {
			bool did_move = false;
			switch (xmetric) {
			case 0:
				if ((stage2_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2 = cube1;
					cube2.do_move (mov_idx);
					next_ms = stage2_stm_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((stage2_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_twist_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 2:
				if ((stage2_block_moves_to_try[move_state] & (1 << stage2_btm_mtt_idx[mov_idx])) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_block_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_block_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_btm_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 0 + 3:
				if ((stage2_slice_moves_to_try_old[move_state] & (1 << mov_idx)) != 0) {
					cube2 = cube1;
					cube2.do_move (mov_idx);
					next_ms = stage2_stm_next_ms_old[move_state][mov_idx];
					did_move = true;
				}
				break;
			}
			if (did_move) {
				mc = mov_idx;
				switch (metric) {
				case 1:
					mc = stage2_twist_map1[mov_idx];
					break;
				case 2:
					mc = stage2_block_map[mov_idx];
					break;
				}
				move_list[moves_done] = mc;
				if (treesearchSTAGE2 (cube2, depth - 1, moves_done + 1,
							next_ms, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
	}
	return false;
}
#endif
#endif

#ifdef DO_SOLVE_STAGE3
int
get_mod_distanceSTAGE3 (const CubeStage3& cube1, int metric)
{
	int x;
	char fname[64];
	CubeStage3 repcube;
	if (metric != 1) {
		printf ("Metric #%d not supported in get_mod_distanceSTAGE3\n", metric);
		exit (1);
	}
	cube1.get_representative (&repcube, NULL);
	UINT edge = stage3_edge_to_edgesym[repcube.m_edge]/N_SYM_STAGE3;
	UINT center = repcube.m_centerLR;
	int fdist = 18;
	FILE* f = NULL;
	FILE* fx = NULL;
	const char* dist_str = "distm15";
	if (stg3_file == NULL) {
		sprintf (&fname[0], "%sstage3_%s_%s_%02d.rbk", &datafiles_path[0], metric_names[metric], dist_str, fdist);

		f = fopen (&fname[0], "rb");
		if (f == NULL) {
			printf ("could not open '%s'\n", &fname[0]);
			exit (1);
		}
		fx = f;
	} else {
		fx = stg3_file;
	}

	UINT idx = N_STAGE3_CENTER_CONFIGS*edge + center;
	if (cube1.m_edge_odd) {
		idx += N_STAGE3_EDGE_SYMCONFIGS*N_STAGE3_CENTER_CONFIGS;
	}
	UINT idx2 = idx/2u;
	int idxj = idx & 0x1;
	int result = fseek (fx, static_cast<long>(idx2), SEEK_SET);
	if (result != 0) {
		UINT i;
		for (i = 0; i < idx2; ++i) {
			x = getc (fx);
			if (x == EOF) {
				printf ("Unexpected EOF in file\n");
				return 31;
			}
		}
	}
	x = getc (fx);
	if (x == EOF) {
		printf ("Unexpected EOF in file\n");
	}
	if (f != NULL) {
		fclose (f);
	}
	if (idxj == 0) {
		return x & 0xF;
	}
	return (x >> 4) & 0xF;
}

#ifdef PRUNING_TABLES
int
solveitIDA_STAGE3 (const CubeStage3& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE3 (init_cube, g1, 0, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

UINT tss3x = 0x3;

bool
treesearchSTAGE3 (const CubeStage3& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage3 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			if (got_interrupt) {
				throw 1;
			}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0;
	if ((tss3x & 0x1) != 0) {
		dist = prune_funcCEN_STAGE3 (cube1);
	}
	if ((tss3x & 0x2) != 0 && dist <= depth) {
		dist = prune_funcEDGE_STAGE3 (cube1);
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < N_STAGE3_2TWIST_MOVES; ++mov_idx) {
				cube2 = cube1;
				mc = stage3_2twist_moves[mov_idx][0];		//!!! metric dependency
				cube2.do_move (mc);
				mc = stage3_2twist_moves[mov_idx][1];
				if (mc >= 0) {
					cube2.do_move (mc);
				}
				move_list[moves_done] = stage3_twist_map1[N_STAGE3_TWIST_MOVES + mov_idx];
				move_list[moves_done + 1] = stage3_twist_map2[N_STAGE3_TWIST_MOVES + mov_idx];
				if (treesearchSTAGE3 (cube2, depth - 2, moves_done + 2,
						0, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg3[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				//TODO: This not finished.
				for (j = 0; stage3_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				//TODO: This not finished.
				for (j = 0; stage3_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage3_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage3_block_map[mov_idx];
				break;
			}
			move_list[moves_done] = mc;
			if (treesearchSTAGE3 (cube2, depth - 1, moves_done + 1,
						0, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}
#endif
#endif

#ifdef DO_SOLVE_STAGE4

#ifdef PRUNING_TABLES
int
solveitIDA_STAGE4 (const CubeStage4& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE4 (init_cube, g1, 0, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

UINT stg4_prune_bits = 0x3;

bool
treesearchSTAGE4 (const CubeStage4& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage4 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			if (got_interrupt) {
				throw 1;
			}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0;
	if ((stg4_prune_bits & 0x1) != 0) {
		dist = prune_funcCENCOR_STAGE4 (cube1);
	}
	if (dist <= depth && (stg4_prune_bits & 0x2) != 0) {
		dist = prune_funcEDGCEN_STAGE4 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg4[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				for (j = 0; stage4_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				for (j = 0; stage4_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage4_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage4_block_map[mov_idx];
				break;
			}
			move_list[moves_done] = mc;
			if (treesearchSTAGE4 (cube2, depth - 1, moves_done + 1,
						0, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}
#endif
#endif

#ifdef DO_SOLVE_WHOLE_CUBE
int
solveit4x4x4IDA (const CubeState& init_cube, int* move_list, int metric)
{
	int xmove_list[64];
	int xcount = 0;

	int i;
	bool usexm = false;
	CubeStage1 s1;
	CubeStage2 s2;
	CubeStage3 s3;
	CubeStage4 s4;
	CubeSqsCoord s5;
	s1.init ();
	s2.init ();
	s3.init ();
	s4.init ();
	s5.init ();
	convert_std_cube_to_stage1 (init_cube, &s1);
	int count1 = solveitIDA_STAGE1 (s1, move_list, metric);
	if (count1 < 0 || count1 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	int count = count1;
	CubeState cube1 = init_cube;
	switch (metric) {
	case 0:
		break;
	case 1:
		for (i = 0; i < count1; ++i) {
			move_list[i] = stage1_twist_list[move_list[i]];
		}
		break;
	case 2:
		for (i = 0; i < count1; ++i) {
			move_list[i] = stage1_block_list[move_list[i]];
		}
		break;
	}
	if (show_per_stage_move_lists) {
		printf ("Stage1: ");
		print_move_list (count1, move_list);
		printf ("\n");
	}

	scrambleCUBE (&cube1, count, move_list);
	int r3 = cube1.m_cor[0] >> 3;
	switch (r3) {
	case 0:
		break;	//no whole cube rotation
	case 1:
		cube1.do_move (Lf3);
		cube1.do_move (Ls3);
		cube1.do_move (Rs);
		cube1.do_move (Rf);
		cube1.do_move (Uf3);
		cube1.do_move (Us3);
		cube1.do_move (Ds);
		cube1.do_move (Df);
		break;
	case 2:
		cube1.do_move (Ff);
		cube1.do_move (Fs);
		cube1.do_move (Bs3);
		cube1.do_move (Bf3);
		cube1.do_move (Uf);
		cube1.do_move (Us);
		cube1.do_move (Ds3);
		cube1.do_move (Df3);
		break;
	default:
		printf ("Invalid cube rotation state.\n");
		exit (1);
	}

	convert_std_cube_to_stage2 (cube1, &s2);
    int count2 = solveitIDA_STAGE2 (s2, &move_list[count], metric);
	if (count2 < 0 || count2 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube2 = cube1;
	//translate from stage2 move indices to standard move codes
	if (metric == 0) {
		for (i = count; i < count + count2; ++i) {
			move_list[i] = stage2_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube2, count2, &move_list[count]);

	//translate move codes for whole cube rotations performed
	for (i = count; i < count + count2; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r3];
	}
	if (show_per_stage_move_lists) {
		printf ("Stage2: ");
		print_move_list (count2, &move_list[count]);
		printf ("\n");
	}

	count += count2;

	int r6 = r3;
	if (cube2.m_cen[16] < 4) {
		cube2.do_move (Uf);
		cube2.do_move (Us);
		cube2.do_move (Ds3);
		cube2.do_move (Df3);
		r6 += 3;
	}
	convert_std_cube_to_stage3 (cube2, &s3);
	s3.m_edge_odd = cube2.edgeUD_parity_odd ();
	int count3 = solveitIDA_STAGE3 (s3, &move_list[count], metric);
	if (count3 < 0 || count3 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube3 = cube2;
	if (metric == 0) {
		for (i = count; i < count + count3; ++i) {
			move_list[i] = stage3_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube3, count3, &move_list[count]);
	for (i = count; i < count + count3; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	if (show_per_stage_move_lists) {
		printf ("Stage3: ");
		print_move_list (count3, &move_list[count]);
		printf ("\n");
	}
	count += count3;

	convert_std_cube_to_stage4 (cube3, &s4);
	int count4 = solveitIDA_STAGE4 (s4, &move_list[count], metric);
	if (count4 < 0 || count4 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube4 = cube3;
	if (metric == 0) {
		for (i = count; i < count + count4; ++i) {
			move_list[i] = stage4_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube4, count4, &move_list[count]);
	for (i = count; i < count + count4; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	if (show_per_stage_move_lists) {
		printf ("Stage4: ");
		print_move_list (count4, &move_list[count]);
		printf ("\n");
	}
	count += count4;

#ifdef SQUARES_INIT
	convert_std_cube_to_squares (cube4, &s5);
	int count5 = solveitIDA_SQS (s5, &move_list[count], metric);
	if (count5 < 0 || count5 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	if (metric == 0) {
		for (i = count; i < count + count5; ++i) {
			move_list[i] = sq_moves[move_list[i]];
		}
	}
	for (i = count; i < count + count5; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	if (show_per_stage_move_lists) {
		printf ("Stage5: ");
		print_move_list (count5, &move_list[count]);
		printf ("\n");
	}
	count += count5;
#endif
	return count;
}

void
open_distance_files (int metric)
{
	if (metric == 2) {
		return;		//!!!
	}
	static char fname_s1[320];
	static char fname_s2[320];
	static char fname_s3[320];
	static char fname_s4[320];
	static char fname_s5[320];

	bool old_move_set = false;	//for limited set of moves for ttm, stages 2 & 3
	int fdist_stg1 = 11;
	int fdist_stg2 = 16;
	int fdist_stg3 = 14;
	int fdist_stg4 = 17;
	int fdist_stg5 = 19;

	int stg2_fmod[7] = { 4, 6, 4, 0, 0, 0, 4 };
	int stg3_fmod[7] = { 0, 15, 0, 0, 0, 0, 0 };
	if (metric == 0 && use_old_stage2) {
		fdist_stg2 = 18;
	}
	if (metric == 1) {
		fdist_stg1 = 11;
		fdist_stg2 = 18;
		fdist_stg3 = 18;
		fdist_stg4 = 16;
		fdist_stg5 = 20;
		if (old_move_set) {
			fdist_stg2 = 19;
			fdist_stg3 = 19;
		}
	} else if (metric == 2) {
		fdist_stg1 = 10;
		fdist_stg2 = 15;
		fdist_stg3 = 12;
		fdist_stg4 = 15;

		fdist_stg5 = 16;
	}
	sprintf (&fname_s1[0], "%sstage1_%s_dist_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist_stg1);
	stg1_file = fopen (&fname_s1[0], "rb");
	if (stg1_file == NULL) {
		printf ("could not open '%s'\n", &fname_s1[0]);
	}

	if (stg2_fmod[metric] == 0) {
		sprintf (&fname_s2[0], "%sstage2_%s_dist_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist_stg2);
	} else {
		sprintf (&fname_s2[0], "%sstage2_%s_distm%d_%02d.rbk", &datafiles_path[0], metric_names[metric],
			stg2_fmod[metric], fdist_stg2);
	}
	stg2_file = fopen (&fname_s2[0], "rb");
	if (stg2_file == NULL) {
		printf ("could not open '%s'\n", &fname_s2[0]);
	}

	if (stg3_fmod[metric] == 0) {
		sprintf (&fname_s3[0], "%sstage3_%s_dist_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist_stg3);
	} else {
		sprintf (&fname_s3[0], "%sstage3_%s_distm%d_%02d.rbk", &datafiles_path[0], metric_names[metric],
			stg3_fmod[metric], fdist_stg3);
	}
	stg3_file = fopen (&fname_s3[0], "rb");
	if (stg3_file == NULL) {
		printf ("could not open '%s'\n", &fname_s3[0]);
	}

#ifdef USE_SYMMETRY
	sprintf (&fname_s4[0], "%sstage4_%s_dist_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist_stg4);
	stg4_file = fopen (&fname_s4[0], "rb");
	if (stg4_file == NULL) {
		printf ("could not open '%s'\n", &fname_s4[0]);
	}
#endif

	sprintf (&fname_s5[0], "%ssquares_%s_distm4_%02d.rbk", &datafiles_path[0], metric_names[metric], fdist_stg5);
	stg5_file = fopen (&fname_s5[0], "rb");
	if (stg5_file == NULL) {
		printf ("could not open '%s'\n", &fname_s5[0]);
	}
}

void
close_distance_files ()
{
	if (stg1_file != NULL) {
		fclose (stg1_file);
		stg1_file = NULL;
	}
	if (stg2_file != NULL) {
		fclose (stg2_file);
		stg2_file = NULL;
	}
	if (stg3_file != NULL) {
		fclose (stg3_file);
		stg3_file = NULL;
	}
	if (stg4_file != NULL) {
		fclose (stg4_file);
		stg4_file = NULL;
	}
	if (stg5_file != NULL) {
		fclose (stg5_file);
		stg5_file = NULL;
	}
}
#endif

UINT
sym_on_cp96 (UINT cp96, UINT sym)
{
	CubeSqsCoord cube1, cube2;
	cube1.init ();
	cube1.m_cp96 = cp96;
	reorient_cubeSQS (cube1, sym, &cube2);
	return cube2.m_cp96;
}

UINT
sym_on_cen12x12x12 (UINT cen12x12x12, UINT sym)
{
	CubeSqsCoord cube1, cube2;
	cube1.init ();
	cube1.m_cen12x12x12 = cen12x12x12;
	reorient_cubeSQS (cube1, sym, &cube2);
	return cube2.m_cen12x12x12;
}

void
init_4of8 ()
{
	static UINT bitcount[16] = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
	int a1, a2, a3, a4;
	int i;
	int count = 0;
	for (i = 0; i < 256; ++i) {
		bm4of8_to_70[i] = 99;
	}
	for (a1 = 0; a1 < 8-3; ++a1) {
		for (a2 = a1+1; a2 < 8-2; ++a2) {
			for (a3 = a2+1; a3 < 8-1; ++a3) {
				for (a4 = a3+1; a4 < 8; ++a4) {
					bm4of8[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4);
					bm4of8_to_70[bm4of8[count]] = count;
					++count;
				}
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("4of8 count = %d\n", count);
#endif
	count = 0;
	for (i = 0; i < 256; ++i) {
		if (bitcount[i & 0xF] == bitcount[(i >> 4) & 0xF]) {
			wi4of8_bm_to_idx[i] = count++;
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("wi4of8 count = %u\n", count);
#endif
}

#ifdef ELOC_INIT
void
init_eloc ()
{
	static UINT bitcount[16] = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
	const UINT POW2_24 = 4096*4096;
	int a1, a2, a3, a4, a5, a6, a7, a8;
	int i;
	USHORT u;
	Face t[8];
	Face f;
	int count = 0;
	for (a1 = 0; a1 < POW2_24; ++a1) {
		ebm2eloc[a1] = 999999;
	}
	for (a1 = 0; a1 < 24-7; ++a1) {
	 for (a2 = a1 + 1; a2 < 24-6; ++a2) {
	  for (a3 = a2 + 1; a3 < 24-5; ++a3) {
	   for (a4 = a3 + 1; a4 < 24-4; ++a4) {
	    for (a5 = a4 + 1; a5 < 24-3; ++a5) {
	     for (a6 = a5 + 1; a6 < 24-2; ++a6) {
	      for (a7 = a6 + 1; a7 < 24-1; ++a7) {
	       for (a8 = a7 + 1; a8 < 24; ++a8) {
				eloc2ebm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
					(1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
				ebm2eloc[eloc2ebm[count]] = count++;
	       }
	      }
	     }
		}
	   }
	  }
	 }
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("Eloc count is %d\n", count);
#endif
	for (a1 = 0; a1 < 24; ++a1) {
		perm_n_unpack (4, a1, &t[0]);
		for (i = 0; i < 4; ++i) {
			t[i+4] = t[i] + 4;
		}
		for (i = 0; i < 8; ++i) {
			map96[4*a1][i] = t[i];
		}
		f = t[4]; t[4]= t[5]; t[5] = f;
		f = t[6]; t[6]= t[7]; t[7] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 1][i] = t[i];
		}
		f = t[4]; t[4]= t[7]; t[7] = f;
		f = t[5]; t[5]= t[6]; t[6] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 2][i] = t[i];
		}
		f = t[4]; t[4]= t[5]; t[5] = f;
		f = t[6]; t[6]= t[7]; t[7] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 3][i] = t[i];
		}
	}
//optimization in progress...
	for (u = 0; u < 4096; ++u) {
		UINT bc = bitcount[u & 0xF] + bitcount[(u >> 4) & 0xF] + bitcount[(u >> 8) & 0xF];
		UINT u1;
		for (u1 = 0; u1 < 70; ++u1) {
			UINT u12 = u;
			UINT bbbb = bm4of8[u1];
			int j1 = 0;
			int j2;
			for (i = 0; u12 != 0; ++i) {
				if ((u12 & 0x1) != 0) {
					if ((bbbb & 0x1) == 0x1) {
						t[j1++] = i;
					}
					bbbb >>= 1;
				}
				u12 >>= 1;
			}
			a1 = 0;
			a2 = 24*24*24;
			for (j2 = 0; j2 < j1; ++j2) {
				a1 += a2*t[j2];
				a2 /= 24;
			}
			bm12_4of8_to_low_idx[u][u1] = a1;
			u12 = u;
			bbbb = bm4of8[u1];
			j1 = 0;
			for (i = 24 - 1; u12 != 0; --i) {
				if ((u12 & 0x800) != 0) {
					if ((bbbb & 0x80) != 0) {
						t[j1++] = i;
					}
					bbbb <<= 1;
				}
				u12 <<= 1;
				u12 &= 0xFFF;		//need this to become 0 after no more than 12 iterations
			}
			a1 = 0;
			a2 = 1;
			for (j2 = 0; j2 < j1; ++j2) {
				a1 += a2*t[j2];
				a2 *= 24;
			}
			bm12_4of8_to_high_idx[u][u1] = a1;
		}
	}
	for (u = 0; u < 256; ++u) {
		UINT u1;
		int bc = countbits (u);
		bitcount8[u] = bc;
		for (u1 = 0; u1 < 256; ++u1) {
			UINT u0 = u;
			UINT u3 = 0;
			UINT b = 0x1;
			for (i = 0; i < 8 && u0 != 0; ++i) {
				if ((u0 & 0x1) != 0) {
					if ((u1 & (1 << i)) != 0) {
						u3 |= b;
					}
					b <<= 1;
				}
				u0 >>= 1;
			}
			gen_MofN8[u][u1] = u3;
		}
	}
}
#endif

#ifdef STAGE1_INIT
void
init_stage1 ()
{
	int i;
	UINT u, sym1;
	CubeStage1 cube1;
	CubeStage1 cube2;
	cube1.init ();
	cube2.init ();
	for (u = 0; u < N_CORNER_ORIENT; ++u) {
		cube1.m_co = u;
		for (sym1 = 0; sym1 < 16; ++sym1) {
            reorient_cubeSTAGE1_slow (cube1, sym1, &cube2);
			reorient_co[u][sym1] = cube2.m_co;
		}
	}
	cube1.init ();
	for (u = 0; u < N_EDGE_COMBO8; ++u) {
		cube1.m_edge_ud_combo8 = u;
		for (sym1 = 0; sym1 < 16; ++sym1) {
            reorient_cubeSTAGE1_slow (cube1, sym1, &cube2);
			reorient_s1edge[u][sym1] = cube2.m_edge_ud_combo8;
		}
	}
}
#endif

#ifdef STAGE1_INIT
void
init_move_tablesSTAGE1 ()
{
	int i, mc, lrfb, ud;
	UINT u;
	CubeState cube1, cube2;
	CubeStage1 s1, s2;
	s1.init ();
	s2.init ();
	cube1.init ();
	cube2.init ();
	for (u = 0; u < N_EDGE_COMBO8; ++u) {
		UINT ebm = eloc2ebm[u];
		lrfb = 0;
		ud = 16;
		for (i = 0; i < 24; ++i) {
			if ((ebm & (1 << i)) == 0) {
				cube1.m_edge[i] = lrfb++;
			} else {
				cube1.m_edge[i] = ud++;
			}
		}
		for (mc = 0; mc < N_BASIC_MOVES; ++mc) {
			cube2 = cube1;
			cube2.do_move (mc);
			ebm = 0;
			for (i = 0; i < 24; ++i) {
				if (cube2.m_edge[i] >= 16) {
					ebm |= (1 << i);
				}
			}
			move_table_edgeSTAGE1[u][mc] = ebm2eloc[ebm];
		}
	}
	cube1.init ();
	for (u = 0; u < N_CORNER_ORIENT; ++u) {
		s1.m_co = u;
		convert_stage1_to_std_cube (s1, &cube1);
		for (mc = 0; mc < N_BASIC_MOVES; ++mc) {
			int fmc = basic_to_face[mc];
			if (fmc >= 0) {
				if (fmc >= N_FACE_MOVES) {
					printf ("do_move: face move code error\n");
					exit (1);
				}
				cube2 = cube1;
				rotate_sliceCORNER (mc, cube1, &cube2);
				convert_std_cube_to_stage1 (cube2, &s2);
				move_table_co[u][fmc] = s2.m_co;
			}
		}
	}
}
#endif

void
scrambleSTAGE1 (CubeStage1* pcube, int move_count, const int* move_arr)
{
	int i;
	for (i = 0; i < move_count; ++i) {
		pcube->do_move (move_arr[i]);
	}
}

void
scrambleSTAGE2 (CubeStage2* pcube, int move_count, const int* move_arr)
{
	int i;
	for (i = 0; i < move_count; ++i) {
		CubeStage2 S2;
		S2.init ();
		S2 = *pcube;
		S2.do_move_slow (move_arr[i]);
		pcube->do_move (move_arr[i]);
		if (! S2.compare (*pcube)) {
			printf ("scramble check mis-compare\n");
		}
	}
}

#ifdef CLOC_INIT
void
init_cloc ()
{
	static UINT bitcount[16] = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
	const UINT POW2_24 = 4096*4096;
	int a1, a2, a3, a4; //, a5, a6, a7, a8;
	int count = 0;

	count = 0;
	for (a1 = 0; a1 < 24-3; ++a1) {
	 UINT bm1 = 1 << a1;
	 for (a2 = a1 + 1; a2 < 24-2; ++a2) {
	  UINT bm2 = bm1 | (1 << a2);
	  for (a3 = a2 + 1; a3 < 24-1; ++a3) {
	   UINT bm3 = bm2 | (1 << a3);
	   for (a4 = a3 + 1; a4 < 24; ++a4) {
		UINT bm4 = bm3 | (1 << a4);
		cloc_to_bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4);
		c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a3 + a4] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a4 + a3] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a2 + a4] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a4 + a2] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a2 + a3] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a3 + a2] = count;

		c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a3 + a4] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a4 + a3] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a1 + a4] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a4 + a1] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a1 + a3] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a3 + a1] = count;

		c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a2 + a4] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a4 + a2] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a1 + a4] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a4 + a1] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a1 + a2] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a2 + a1] = count;

		c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a2 + a3] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a3 + a2] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a1 + a3] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a3 + a1] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a1 + a2] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a2 + a1] = count;
		++count;
	   }
	  }
	 }
	}
}
#endif

#ifdef STAGE2_INIT
void
init_stage2 ()
{
	int i, j;
	UINT u, v, w, u2, sym, cloc_f, cloc_b;
	CubeStage2 cs2a, cs2b;
	cs2a.init ();
	cs2b.init ();
	Face t[8];
	Face t2[8];
	Face t3[8];
	for (u = 0; u < 40320; ++u) {
		perm_to_420[u] = 999;
	}
	for (u = 0; u < 70; ++u) {
		UINT bm = bm4of8[u];
		for (v = 0; v < 6; ++v) {
			perm_n_unpack (8, v, &t[0]);
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i) {
					t2[i] = map96[w][t[i]];
				}
				int f = 0;
				int b = 4;
				for (i = 0; i < 8; ++i) {
					if ((bm & (1 << i)) == 0) {
						t3[i] = t2[b++];
					} else {
						t3[i] = t2[f++];
					}
				}
				u2 = perm_n_pack (8, &t3[0]);
				perm_to_420[u2] = 6*u + v;
			}
		}
	}
	for (u = 0; u < 40320; ++u) {
		if (perm_to_420[u] >= 420) {
			printf ("perm to 420 error %u\n", u);
		}
	}
	CubeState cube2;
	double goodcount = 0.0;
	cube2.init ();
	int mc, udlrf;
	CubeState cube1;//, cube2;
	CubeStage2 s1, s2;
	s1.init ();
	s2.init ();
	cube1.init ();
	cube2.init ();
	for (u = 0; u < N_CENTER_COMBO4; ++u) {
		UINT cbm = cloc_to_bm[u];
		udlrf = 0;
		for (i = 0; i < 24; ++i) {
			if ((cbm & (1 << i)) == 0) {
				cube1.m_cen[i] = udlrf++/4;
			} else {
				cube1.m_cen[i] = 5;
			}
		}
		for (mc = 0; mc < N_STAGE2_SLICE_MOVES; ++mc) {
			cube2 = cube1;
			cube2.do_move (stage2_slice_moves[mc]);
			j = 0;
			for (i = 0; i < 24; ++i) {
				if (cube2.m_cen[i] == 5) {
					t[j++] = i;
				}
			}
			int idx = 24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3];
			move_table_cenSTAGE2[u][mc] = c4_to_cloc[idx];
		}
	}
	for (u = 0; u < 420; ++u) {
		s1.m_centerFB = 0;
		s1.m_edge = u;
		for (mc = 0; mc < N_STAGE2_SLICE_MOVES; ++mc) {
			s2 = s1;
			s2.do_move_slow (mc);
			move_table_edgeSTAGE2[u][mc] = s2.m_edge;
		}
	}
	s1.init ();
	s2.init ();

	stab1.set_bit (0);

	for (u = 0; u < N_STAGE2_EDGE_CONFIGS; ++u) {
		stage2_edge_to_edgesym[u] = 65000;
	}
	UINT count = 0;
	for (u = 0; u < N_STAGE2_EDGE_CONFIGS; ++u) {
		if (stage2_edge_to_edgesym[u] == 65000) {
			stage2_edge_to_edgesym[u] = N_SYM_STAGE2*count++;
			stage2_edgesym_to_edge[stage2_edge_to_edgesym[u]] = u;
			for (sym = 1; sym < N_SYM_STAGE2; ++sym) {
				s1.m_edge = u;
				reorient_cubeSTAGE2_slow (s1, sym, &s2);
				UINT u2 = s2.m_edge;
				if (stage2_edge_to_edgesym[u2] == 65000) {
					stage2_edge_to_edgesym[u2] = stage2_edge_to_edgesym[u] + sym;
				} else {
					if (stage2_edge_to_edgesym[u2]/N_SYM_STAGE2 != stage2_edge_to_edgesym[u]/N_SYM_STAGE2) {
						printf ("sym-coodinate generation inconsistency!\n");
						exit (1);
					}
				}
				if (u2 < u) {
					printf ("sym-coordinate generation inconsistency: u2 < u\n");
				}
				stage2_edgesym_to_edge[stage2_edge_to_edgesym[u] + sym] = u2;
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("edge group count = %d\n", count);
#endif
#ifdef USE_CUBE_LIST_TABLE
	s1.init ();
	for (u = 0; u < N_STAGE2_EDGE_SYMCONFIGS; ++u) {
		Stab sym_bm;
		sym_bm.set_bit (0);
		int sym_count = 1;

		s1.m_edge = stage2_edgesym_to_edge[N_SYM_STAGE2*u];
		for (sym = 1; sym < N_SYM_STAGE2; ++sym) {
			UINT cp2, ep2;
			reorient_cubeSTAGE2_slow (s1, sym, &s2);
			if (s2.m_edge < s1.m_edge) {
				printf ("stage 2 edgemap gen error!\n");
				exit (1);
			}
			if (s2.m_edge == s1.m_edge) {
				sym_bm.set_bit (sym);
				++sym_count;
			}
		}
		stage2e_stab[u] = sym_bm;
		stage2e_mult[u] = N_SYM_STAGE2 / sym_count;
	}
#endif
	cs2a.init();
	cs2b.init ();
	for (u = 0; u < N_CENTER_COMBO4; ++u) {
		UINT bm = cloc_to_bm[u];
		j = 0;
		UINT bm2 = 0;
		for (i = 0; i < 8 && j < 4; ++i) {
			if ((bm & (1 << i)) == 0) {
				t[j++] = i;
			}
		}
		UINT c4 = 24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3];
		cs2a.m_centerFB = get_centerFB (u, c4_to_cloc[c4]);
		for (sym = 0; sym < N_SYM_STAGE2; ++sym) {
			reorient_cubeSTAGE2_slow (cs2a, sym, &cs2b);
			get_clocFB (cs2b.m_centerFB, &cloc_f, &cloc_b);
			if ((sym & 0x2) == 0) {
				reorient_cenFB [u][sym] = cloc_f;
			} else {
				reorient_cenFB [u][sym] = cloc_b;
			}
		}
	}
}

void
stage2_cen_to_cloc4s (UINT cen, UINT *pclocf, UINT* pclocb)
{
	UINT cenbm = eloc2ebm[cen / 70];
	UINT cenbm4of8 = bm4of8[cen % 70];
	int idx1 = bm12_4of8_to_high_idx[cenbm >> 12][cen % 70];
	idx1 += bm12_4of8_to_low_idx[cenbm & 0xFFF][cen % 70];
	UINT comp_70 = bm4of8_to_70[(~cenbm4of8) & 0xFF];	//could be a direct lookup
	int idx2 = bm12_4of8_to_high_idx[cenbm >> 12][comp_70];
	idx2 += bm12_4of8_to_low_idx[cenbm & 0xFFF][comp_70];
	*pclocf = c4_to_cloc[idx1];
	*pclocb = c4_to_cloc[idx2];
}

UINT
stage2_cen_move_fast (UINT cen, int move_code)
{
	UINT cenbm = eloc2ebm[cen / 70];
	UINT cenbm4of8 = bm4of8[cen % 70];
	int idx1 = bm12_4of8_to_high_idx[cenbm >> 12][cen % 70];
	idx1 += bm12_4of8_to_low_idx[cenbm & 0xFFF][cen % 70];
	UINT comp_70 = bm4of8_to_70[(~cenbm4of8) & 0xFF];	//could be a direct lookup
	int idx2 = bm12_4of8_to_high_idx[cenbm >> 12][comp_70];
	idx2 += bm12_4of8_to_low_idx[cenbm & 0xFFF][comp_70];
	UINT cloc1 = c4_to_cloc[idx1];
	UINT cloc2 = c4_to_cloc[idx2];
	UINT cloc1b = move_table_cenSTAGE2[cloc1][move_code];
	UINT cloc2b = move_table_cenSTAGE2[cloc2][move_code];
	UINT cbm1b = cloc_to_bm[cloc1b];
	UINT cbm2b = cloc_to_bm[cloc2b];
	UINT cenbm2 = cbm1b | cbm2b;
	UINT bm48lo = gen_MofN8[cenbm2 & 0xFF][cbm1b & 0xFF];
	int bclo = bitcount8[cenbm2 & 0xFF];
	UINT bm48mid = gen_MofN8[(cenbm2 >> 8) & 0xFF][(cbm1b >> 8) & 0xFF];
	int bcmid = bitcount8[(cenbm2 >> 8) & 0xFF];
	UINT bm48hi = gen_MofN8[(cenbm2 >> 16) & 0xFF][(cbm1b >> 16) & 0xFF];
	UINT bm4of8b = (bm48mid << bclo) | bm48lo;
	bm4of8b |= (bm48hi << (bclo + bcmid));
	UINT cen1 = bm4of8_to_70[bm4of8b];
	UINT cen2 = ebm2eloc[cenbm2];
	return 70*cen2 + cen1;
}

UINT
stage2_cen_move (UINT cen, int move_code)
{
	int i;
	Face t1[4];
	Face t2[4];
	UINT cenbm = eloc2ebm[cen / 70];
	UINT cenbm4of8 = bm4of8[cen % 70];
	int j1 = 0;
	int j2 = 0;
	for (i = 0; cenbm != 0; ++i) {
		if ((cenbm & 0x1) != 0) {
			if ((cenbm4of8 & 0x1) == 0) {
				t2[j2++] = i;
			} else {
				t1[j1++] = i;
			}
			cenbm4of8 >>= 1;
		}
		cenbm >>= 1;
	}
	int idx1 = 24*24*24*t1[0] + 24*24*t1[1] + 24*t1[2] + t1[3];
	int idx2 = 24*24*24*t2[0] + 24*24*t2[1] + 24*t2[2] + t2[3];
	UINT cloc1 = c4_to_cloc[idx1];
	UINT cloc2 = c4_to_cloc[idx2];
	UINT cloc1b = move_table_cenSTAGE2[cloc1][move_code];
	UINT cloc2b = move_table_cenSTAGE2[cloc2][move_code];
	UINT cbm1b = cloc_to_bm[cloc1b];
	UINT cbm2b = cloc_to_bm[cloc2b];
	UINT cenbm2 = cbm1b | cbm2b;
	j1 = 0;
	UINT bm4of8b = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm2 & (1 << i)) != 0) {
			if ((cbm1b & (1 << i)) != 0) {
				bm4of8b |= (1 << j1);
			}
			++j1;
		}
	}
	UINT cen1 = bm4of8_to_70[bm4of8b];
	UINT cen2 = ebm2eloc[cenbm2];
	return 70*cen2 + cen1;
}
#endif

#ifdef STAGE3_INIT
void
init_stage3 ()
{
	static UINT bitcount[16] = { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
	const UINT POW2_16 = 256*256;
	int a1, a2, a3, a4, a5, a6, a7, a8;
	int sym;
	USHORT u;
	UINT u1;
	CubeStage3 s1, s2;

	int count = 0;
	for (a1 = 0; a1 < POW2_16; ++a1) {
		e16bm2eloc[a1] = 999999;
	}
	for (a1 = 0; a1 < 16-7; ++a1) {
	 for (a2 = a1 + 1; a2 < 16-6; ++a2) {
	  for (a3 = a2 + 1; a3 < 16-5; ++a3) {
	   for (a4 = a3 + 1; a4 < 16-4; ++a4) {
	    for (a5 = a4 + 1; a5 < 16-3; ++a5) {
	     for (a6 = a5 + 1; a6 < 16-2; ++a6) {
	      for (a7 = a6 + 1; a7 < 16-1; ++a7) {
	       for (a8 = a7 + 1; a8 < 16; ++a8) {
				eloc2e16bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
					(1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
				e16bm2eloc[eloc2e16bm[count]] = count++;
	       }
	      }
	     }
		}
	   }
	  }
	 }
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("Eloc16 count is %d\n", count);
#endif

	s1.init ();
	s2.init ();
	for (u1 = 0; u1 < N_STAGE3_EDGE_CONFIGS; ++u1) {
		s1.m_edge = u1;
		for (sym = 0; sym < N_SYM_STAGE3; ++sym) {
			reorient_cubeSTAGE3_slow (s1, sym, &s2);
			reorient_s3edge[u1][sym] = s2.m_edge;
		}
	}
	s1.init ();
	for (u1 = 0; u1 < N_STAGE3_CENTER_CONFIGS; ++u1) {
		s1.m_centerLR = u1;
		for (sym = 0; sym < N_SYM_STAGE3; ++sym) {
			reorient_cubeSTAGE3_slow (s1, sym, &s2);
			reorient_s3cen[u1][sym] = s2.m_centerLR;
		}
	}
	init_move_tablesSTAGE3 ();

	s1.init ();
	s2.init ();

	stab1.set_bit (0);

	for (u = 0; u < N_STAGE3_EDGE_CONFIGS; ++u) {
		stage3_edge_to_edgesym[u] = 65000;
	}
	count = 0;
	for (u = 0; u < N_STAGE3_EDGE_CONFIGS; ++u) {
		if (stage3_edge_to_edgesym[u] == 65000) {
			stage3_edge_to_edgesym[u] = N_SYM_STAGE3*count++;
			stage3_edgesym_to_edge[stage3_edge_to_edgesym[u]] = u;
			for (sym = 1; sym < N_SYM_STAGE3; ++sym) {
				s1.m_edge = u;
				reorient_cubeSTAGE3 (s1, sym, &s2);
				UINT u2 = s2.m_edge;
				if (stage3_edge_to_edgesym[u2] == 65000) {
					stage3_edge_to_edgesym[u2] = stage3_edge_to_edgesym[u] + sym;
				} else {
					if (stage3_edge_to_edgesym[u2]/N_SYM_STAGE3 != stage3_edge_to_edgesym[u]/N_SYM_STAGE3) {
						printf ("sym-coodinate generation inconsistency!\n");
						exit (1);
					}
				}
				if (u2 < u) {
					printf ("sym-coordinate generation inconsistency: u2 < u\n");
				}
				stage3_edgesym_to_edge[stage3_edge_to_edgesym[u] + sym] = u2;
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("edge group count = %d\n", count);
#endif
}

void
stage3_solved_center_configs ()
{
	UINT solvedset[20000];
	int sqsmovelist[12] = { 2, 3, 6, 7, 8, 9, 10, 11, 12, 15, 16, 19 };
	CubeStage3 s3, s3a;
	s3.init ();
	s3a.init ();
	solvedset[0] = s3.m_centerLR;
	int x, i, j;
	int count = 1;
	int old_count = 0;
	while (count > old_count && count < 1000) {
		printf ("=========\n");
		int start = old_count;
		old_count = count;
		for (x = start; x < old_count; ++x) {
			s3.m_centerLR = solvedset[x];
			for (i = 0; i < 12; ++i) {
				s3a = s3;
				s3a.do_move_slow (sqsmovelist[i]);
				bool found = false;
				for (j = 0; j < count && (! found); ++j) {
					if (s3a.m_centerLR == solvedset[j]) {
						found = true;
					}
				}
				if (! found) {
					solvedset[count++] = s3a.m_centerLR;
					printf (" new: %d ", s3a.m_centerLR);
					s3a.print ();
					printf ("\n");
				}
			}
		}
	}
	printf ("count is %d\n", count);
}

void
init_move_tablesSTAGE3 ()
{
	int mc;
	UINT u;
	CubeStage3 s3;
	s3.init ();
	for (u = 0; u < N_STAGE3_CENTER_CONFIGS; ++u) {
		s3.m_edge = 0;
		if (u == 900830) {
			int xyzabc = 789012;
		}
		for (mc = 0; mc < N_STAGE3_SLICE_MOVES; ++mc) {
			s3.m_centerLR = u;
			s3.do_move_slow (mc);
			move_table_cenSTAGE3[u][mc] = s3.m_centerLR;
		}
	}
	for (u = 0; u < N_STAGE3_EDGE_CONFIGS; ++u) {
		s3.m_centerLR = 0;
		for (mc = 0; mc < N_STAGE3_SLICE_MOVES; ++mc) {
			s3.m_edge = u;
			s3.do_move_slow (mc);
			move_table_edgeSTAGE3[u][mc] = s3.m_edge;
		}
	}
}

#endif

USHORT map8a[40320];
USHORT map8b[40320];
USHORT maplr[1680*1680];
USHORT maplr_U[1680*1680];
USHORT maplr_U3[1680*1680];
USHORT maplr_D[1680*1680];
USHORT maplr_D3[1680*1680];
int countlr[1680*1680];

void
perm3 (UINT u, Face* t)
{
	Face f;
	bool flag = false;
	switch (u) {
	case 0u:
		break;
	case 1u:
		flag = true;
		break;
	case 2u:
		f = t[0];
		t[0] = t[1];
		t[1] = f;
		break;
	case 3u:
		f = t[0];
		t[0] = t[1];
		t[1] = f;
		flag = true;
		break;
	case 4u:
		f = t[0];
		t[0] = t[2];
		t[2] = f;
		flag = true;
		break;
	case 5u:
		f = t[0];
		t[0] = t[2];
		t[2] = f;
		break;
	default:
		printf ("perm3: unexpected parameter\n");
		exit (1);
	}
	if (flag) {
		f = t[1];
		t[1] = t[2];
		t[2] = f;
	}
}

void
array8_to_set_a (const Face* t, CubeState* result_cube)
{
	int i;
	int j = 0;
	for (i = 0; i < 8; ++i) {
		if (i >= 4) {
			j = i + 8;
		} else {
			j = i;
		}
		Face t1 = t[i];
		if (t1 >= 4) {
			t1 += 8;
		}
		result_cube->m_edge[j] = t1;
	}
}

void
set_a_to_array8 (const CubeState& init_cube, Face* t)
{
	int i;
	int j = 0;
	for (i = 0; i < 8; ++i) {
		if (i >= 4) {
			j = i + 8;
		} else {
			j = i;
		}
		Face t1 = init_cube.m_edge[j];
		if (t1 >= 4) {
			if (t1 >= 12) {
				t1 -= 8;
			} else {
				printf ("error: set_a_to_packed8\n");
				exit (1);
			}
		}
		t[i] = t1;
	}
}

void
array8_to_set_b (const Face* t, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		result_cube->m_edge[4 + i] = t[i] + 4;
	}
}

void
set_b_to_array8 (const CubeState& init_cube, Face* t)
{
	int i;
	for (i = 0; i < 8; ++i) {
		t[i] = init_cube.m_edge[4 + i] - 4;
	}
}

UINT
cube_state_to_lrfb (const CubeState& init_cube)
{
	Face t[8];
	set_a_to_array8 (init_cube, &t[0]);
	UINT u1 = perm_n_pack (8, &t[0]);
	set_b_to_array8 (init_cube, &t[0]);
	UINT u2 = perm_n_pack (8, &t[0]);
	return 40320*u2 + u1;
}

void
lrfb_to_cube_state (UINT u, CubeState* result_cube)
{
	Face t[8];
	result_cube->init ();
	perm_n_unpack (8, u % 40320, &t[0]);
	array8_to_set_a (&t[0], result_cube);
	perm_n_unpack (8, u / 40320, &t[0]);
	array8_to_set_b (&t[0], result_cube);
}

int
set_a_find96 (UINT u, UINT idx)
{
	int mc, i, q;
	Face t[8], t2[8];
	CubeState cs1, cs2;
	UINT mylist[600];
	int list_count = 0;
	cs1.init ();
	if (map8a[u] == 65000) {
		mylist[0] = u;
		map8a[u] = idx;
		++list_count;
	}
	for (q = 0; q < list_count; ++q) {
		UINT u1 = mylist[q];
		perm_n_unpack (8, u1, &t[0]);
		array8_to_set_a (&t[0], &cs1);
		for (mc = 2; mc < N_BASIC_MOVES; mc += 3) {
			if (mc == Us2 || mc == Ds2) {
				continue;	//u2 and d2: don't affect top/bottom layer edges
			}
			cs2 = cs1;
			cs2.do_move (mc);
			set_a_to_array8 (cs2, &t2[0]);
			UINT u2 = perm_n_pack (8, &t2[0]);
			if (map8a[u2] == 65000) {
				mylist[list_count++] = u2;
				map8a[u2] = idx;
				if (list_count > 598) {
					printf ("list count > 598\n");
					exit (1);
				}
			} else {
				if (map8a[u2] != idx) {
					printf ("map8a inconsistency %u %u\n", u1, u2);
					exit (1);
				}
			}
		}
	}
	return list_count;
}

void
set_a_check ()
{
	UINT u;
	UINT idx = 0;
	for (u = 0; u < 40320; ++u) {
		map8a[u] = 65000;
	}
	for (u = 0; u < 40320; ++u) {
		if (map8a[u] == 65000) {
			int x = set_a_find96 (u, idx++);
			if (x != 96) {
				printf ("u = %u: count is %d\n", u, x);
			}
		}
	}
	printf ("idx count is %u\n", idx);
}

void
lrnum_to_cs (UINT u, CubeState* result_cube)
{
	int i, j;
	Face t1[8];
	Face t2[8];
	Face x[24];
	result_cube->init ();
	for (i = 0; i < 16; ++i) {
		x[i] = 0;
	}
	perm_n_unpack2 (8, 4, u / 1680, &t1[0]);
	perm_n_unpack2 (8, 4, u % 1680, &t2[0]);

	for (i = 0; i < 4; ++i) {
		if (t1[i] >= 4) {
			result_cube->m_edge[i] = t1[i] += 8;
		} else {
			result_cube->m_edge[i] = t1[i];
		}
		x[result_cube->m_edge[i]] = 1;
	}
	for (i = 0; i < 4; ++i) {
		result_cube->m_edge[i + 4] = t2[i] + 4;
		x[result_cube->m_edge[i + 4]] = 1;
	}
	j = 0;
	for (i = 8; i < 24; ++i) {
		while (x[j] == 1) {
			++j;
		}
		result_cube->m_edge[i] = j++;
	}
}

UINT
cs_to_lrnum (const CubeState& init_cube)
{
	int i, j;
	Face t1[8];
	Face t2[8];
	Face x[24];
	for (i = 0; i < 4; ++i) {
		Face f = init_cube.m_edge[i];
		if (f >= 12) {
			t1[i] = f - 8;
		} else {
			if (f >= 4) {
				printf ("cs_to_lrnum error\n");
				exit (1);
			}
			t1[i] = f;
		}
	}
	for (i = 0; i < 4; ++i) {
		t2[i] = init_cube.m_edge[i + 4] - 4;
	}
	UINT u1 = perm_n_pack2 (8, 4, &t1[0]);
	UINT u2 = perm_n_pack2 (8, 4, &t2[0]);
	return 1680*u1 + u2;
}

UINT
lr_neighbor (UINT lrnum, int mc)
{
	CubeState cs1;
	lrnum_to_cs (lrnum, &cs1);
	cs1.do_move (mc);
	return cs_to_lrnum (cs1);
}

int
set_lr_find96 (UINT u, UINT idx)
{
	int mc, i, q;
	CubeState cs1, cs2;
	UINT mylist[600];
	int list_count = 0;
	cs1.init ();
	if (maplr[u] == 65000) {
		mylist[0] = u;
		maplr[u] = idx;
		++list_count;
	}
	for (q = 0; q < list_count; ++q) {
		UINT u1 = mylist[q];
		lrnum_to_cs (u1, &cs1);

		for (mc = 2; mc < N_BASIC_MOVES; mc += 3) {
			if (mc == Us2 || mc == Ds2 || mc == Lf2 || mc == Rf2) {
				continue;	//u2, d2, L2, R2: don't affect l- and r-layer edges
			}
			cs2 = cs1;
			cs2.do_move (mc);
			UINT u2 = cs_to_lrnum (cs2);
			if (maplr[u2] == 65000) {
				mylist[list_count++] = u2;
				maplr[u2] = idx;
				if (list_count > 96) {
					printf ("list count > 96\n");
					exit (1);
				}
			} else {
				if (maplr[u2] != idx) {
					printf ("maplr inconsistency %u %u\n", u1, u2);
					exit (1);
				}
			}
		}
	}
	return list_count;
}

void
set_lr_check ()
{
	UINT N = 1680*1680;
	UINT u, u1, v;
	UINT idx = 0;
	for (u = 0; u < N; ++u) {
		maplr[u] = 65000;
	}
	for (u = 0; u < N; ++u) {
		if (maplr[u] == 65000) {
			int x = set_lr_find96 (u, idx++);
			if (x != 96) {
				printf ("u = %u: count is %d\n", u, x);
			}
		}
	}
	printf ("idx count is %u\n", idx);
	for (u = 0; u < N; ++u) {
		maplr_U[u] = lr_neighbor (u, Uf);
		maplr_U3[u] = lr_neighbor (u, Uf3);
		maplr_D[u] = lr_neighbor (u, Uf);
		maplr_D3[u] = lr_neighbor (u, Uf);
	}
	for (u = 0; u < N; ++u) {
#ifdef USE_TITLE_BAR
		if (u % 10 == 0) {
			char str[64];
			sprintf (&str[0], "title lrcheck %u", u);
			system (&str[0]);
		}
#endif
		for (u1 = 0; u1 < N; ++u1) {
			if (maplr[u1] != maplr[u]) {
				continue;
			}
			if (maplr_U[u1] == maplr_U[u] && maplr_U3[u1] == maplr_U3[u] &&
					maplr_D[u1] == maplr_D[u] && maplr_D3[u1] == maplr_D3[u])
			{
				++countlr[u];
			}
		}
	}
	int minval = 9999999;
	int maxval = 0;
	for (u = 0; u < N; ++u) {
		if (countlr[u] < minval) {
			minval = countlr[u];
		}
		if (countlr[u] > maxval) {
			maxval = countlr[u];
		}
	}
	printf ("setlr check min count %u max count %u\n", minval, maxval);
}

UINT repval = 1700000000;
UINT repval_Abm = 0;
UINT repval_Alr6 = 0;
UINT repval_Alr1 = 0;
UINT repval_Afb6 = 0;
UINT repval_Afb1 = 0;
UINT repval_Bbm = 0;
UINT repval_Blr6 = 0;
UINT repval_Blr1 = 0;
UINT repval_Bfb6 = 0;
UINT repval_Bfb1 = 0;
UINT repval_lr_Agoesto = 0;
UINT repval_fb_Agoesto = 0;


UINT
lrfb_get_edge_rep (UINT u) //, Bitvec* bv)
{
#ifdef STAGE4_INIT
	int mc, i, j, q;
	UINT h, h1, h2;
	UINT rep = stage4_edge_hB[u/40320];	//65000*40320;
	UINT reph = stage4_edge_hgB[u/40320];	//65000;
	UINT Blr, Bfb;
	Blr = rep / 24;
	Bfb = rep % 24;
	UINT repBlr = sqs_perm_to_rep[Blr];
	UINT repBfb = sqs_perm_to_rep[Bfb];
	UINT repl = stage4_edge_hgA[u % 40320][6*repBlr + repBfb];
	return 40320*reph + repl;
#else
	printf ("Stage 4 not set up.\n");
	exit (1);
	return 0;
#endif
}

#ifdef STAGE4_INIT
void
init_stage4_edge_tables ()
{
	int mc, i, j, q;
	UINT u, h, h1, h2;
	CubeState cs1, cs2, cs3;
	cs1.init ();
	cs2.init ();
	for (u = 0; u < 40320; ++u) {
#ifdef USE_TITLE_BAR
		if (u % 1000 == 0) {
			char str[64];
			sprintf (&str[0], "title hB hgB %u", u);
			system (&str[0]);
		}
#endif
		cs2.init ();
		lrfb_to_cube_state (40320*u, &cs2);
		UINT rep = 999;
		UINT reph = 65000;
		UINT Blr, Bfb;
		for (h1 = 0; h1 < 576; ++h1) {
			Blr = h1 / 24;
			Bfb = h1 % 24;
			perm_n_unpack (4, Blr, &cs1.m_edge[4]);
			perm_n_unpack (4, Bfb, &cs1.m_edge[8]);
			for (i = 4; i < 8; ++i) {
				cs1.m_edge[i] += 4;
			}
			for (i = 8; i < 12; ++i) {
				cs1.m_edge[i] += 8;
			}
			cs3.compose_edge (cs1, cs2);
			UINT u3 = cube_state_to_lrfb (cs3);
			UINT u3h = u3/40320;
			if (u3h < reph) {
				reph = u3h;
				rep = h1;
			}
		}
		stage4_edge_hB[u] = rep;
		stage4_edge_hgB[u] = reph;
	}
	cs1.init ();
	for (u = 0; u < 40320; ++u) {
#ifdef USE_TITLE_BAR
		if (u % 1000 == 0) {
			char str[64];
			sprintf (&str[0], "title hA hgA %u", u);
			system (&str[0]);
		}
#endif
		cs2.init ();
		lrfb_to_cube_state (u, &cs2);
		UINT Blr, Bfb;
		for (h1 = 0; h1 < 36; ++h1) {
			UINT repl = 65000;
			UINT replr = h1 / 6;
			UINT repfb = h1 % 6;
			for (h2 = 0; h2 < 16; ++h2) {
				perm_n_unpack (4, sqs_rep_to_perm[replr][h2/4], &cs1.m_edge[0]);
				perm_n_unpack (4, sqs_rep_to_perm[repfb][h2%4], &cs1.m_edge[12]);
				for (i = 12; i < 16; ++i) {
					cs1.m_edge[i] += 12;
				}
				cs3.compose_edge (cs1, cs2);
				UINT u3 = cube_state_to_lrfb (cs3);
				UINT u3l = u3 % 40320;
				if (u3l < repl) {
					repl = u3l;
				}
			}
			stage4_edge_hgA[u][h1] = repl;
		}
	}
}

void
check_lrfb_elt (UINT u, bool is_rep)
{
	int i;
	static UINT rep_counter = 0;
	static int count0A0A = 0;
	static int countlr0A0A = 0;
	static int countlrA_0A = 0;
	CubeState cs1;
	Face t[8];
	cs1.init ();
	lrfb_to_cube_state (u, &cs1);
	set_a_to_array8 (cs1, &t[0]);
	UINT bm = 0;
	for (i = 0; i < 8; ++i) {
		if (t[i] < 4) {
			bm |= (1 << i);
		}
	}
	UINT Abm = bm;
	UINT Alr = perm_n_pack (4, &t[0]);
	UINT Afb = perm_n_pack (4, &t[4]);
	UINT Alr6 = sqs_perm_to_rep[Alr];
	UINT Alr1 = Alr/6;
	UINT Afb6 = sqs_perm_to_rep[Afb];
	UINT Afb1 = Afb/6;
	set_b_to_array8 (cs1, &t[0]);
	bm = 0;
	for (i = 0; i < 8; ++i) {
		if (t[i] < 4) {
			bm |= (1 << i);
		}
	}
	UINT Bbm = bm;
	UINT Blr = perm_n_pack (4, &t[0]);
	UINT Bfb = perm_n_pack (4, &t[4]);
	UINT Blr6 = sqs_perm_to_rep[Blr];
	UINT Blr1 = Blr/6;
	UINT Bfb6 = sqs_perm_to_rep[Bfb];
	UINT Bfb1 = Bfb/6;
	if (is_rep) {
		++rep_counter;
		if (! (count0A0A == 16 || count0A0A == 256 || count0A0A == 0 || count0A0A == 576)) {
			printf ("AA0a0a count not 0,16,256,or576 rep %u (%d)\n", rep_counter - 1u, count0A0A);
		}
		if (! (countlr0A0A == 0 || countlr0A0A == 16 || countlr0A0A == 64 ||
			countlr0A0A == 96 || countlr0A0A == 192 || countlr0A0A == 256 ||
			countlr0A0A == 384 || countlr0A0A == 576)) {
			printf ("lr0a0a count not 0,16,64,96,192,256,384or576 rep %u (%d)\n", rep_counter - 1u, countlr0A0A);
		}
		if (! (countlrA_0A == 0 || countlrA_0A == 384 || countlrA_0A == 1536 || countlrA_0A == 2304)) {
			printf ("lrA0a count not 384,1536 rep %u (%d)\n", rep_counter - 1u, countlrA_0A);
		}
		count0A0A = 0;
		countlr0A0A = 0;

		countlrA_0A = 0;
		repval = u;
		repval_Abm = Abm;
		repval_Alr6 = Alr6;
		repval_Alr1 = Alr1;
		repval_Afb6 = Afb6;
		repval_Afb1 = Afb1;
		repval_Bbm = Bbm;
		repval_Blr6 = Blr6;
		repval_Blr1 = Blr1;
		repval_Bfb6 = Bfb6;
		repval_Bfb1 = Bfb1;
		repval_lr_Agoesto = sqs_rep_revmap[Alr6][Blr6];
		repval_fb_Agoesto = sqs_rep_revmap[Afb6][Bfb6];
	} else {
		if (Abm != repval_Abm) {
			printf ("Abm mismatch %10u (%10u) %3u vs %3u\n", u, repval, Abm, repval_Abm);
		}
		if (Bbm != repval_Bbm) {
			printf ("Bbm mismatch %10u (%10u) %3u vs %3u\n", u, repval, Bbm, repval_Bbm);
		}
	}
	if (Alr == 0 && Afb == 0) {
		++count0A0A;
	}
	if (Alr == 0 && Blr == 0) {
		++countlr0A0A;
	}
	if (Alr == 0) {
		++countlrA_0A;
	}
}

UINT
lrfb_find9216 (UINT u, Bitvec* bv, UINT* mylist)
{
	int mc, i, j, q;
	int h;
	CubeState cs1, cs2, cs3;
	cs1.init ();
	cs2.init ();
	cs3.init ();
	lrfb_to_cube_state (u, &cs2);
	mylist[0] = u;
	bv->setb (u);
	for (h = 1; h < N_STAGE4_RAW_EDGE_SOLVED_CONFIGS; ++h) {
		UINT u1 = stage4_solved_edge_configs[h];
		lrfb_to_cube_state (u1, &cs1);
		cs3 = cs1;
		cs3.compose_edge (cs1, cs2);
		UINT u3 = cube_state_to_lrfb (cs3);
		if (! bv->tstb (u3)) {
			mylist[h] = u3;
			bv->setb (u3);
		} else {
			//This version should not reach same element more than once
			printf ("lrfb_find9216 inconsistency!\n");
			exit (1);
		}
	}
	return N_STAGE4_RAW_EDGE_SOLVED_CONFIGS;	//9216
}

void
lrfb_check ()
{
	UINT N = 1680*1680;
	UINT c1, c2, u1, u2, v;
	UINT idx = 0;
	int i;
	UINT mylist[N_STAGE4_RAW_EDGE_SOLVED_CONFIGS];
	Face t1[8], t2[8];
	Face tx[8];
	CubeState cs1, cs2;
	cs1.init ();
	cs2.init ();

	stage4_edge_table_init ();
	if (false) {
		Face t4[16];
		UINT Alr, Blr, Afb, Bfb;
		Alr = 0; Blr = 0; Afb = 0; Bfb = 0;
		perm_n_unpack (4, Alr, &t4[0]);
		perm_n_unpack (4, Afb, &t4[12]);
		perm_n_unpack (4, Blr, &t4[4]);
		perm_n_unpack (4, Bfb, &t4[8]);
		for (i = 0; i < 16; ++i) {
			cs1.m_edge[i] = t4[i] + (4*(i/4));
		}
		UINT u = cube_state_to_lrfb (cs1);
		printf ("first: %10u\n", u);
		Afb = 19;
		perm_n_unpack (4, Alr, &t4[0]);
		perm_n_unpack (4, Afb, &t4[12]);
		perm_n_unpack (4, Blr, &t4[4]);
		perm_n_unpack (4, Bfb, &t4[8]);
		for (i = 0; i < 16; ++i) {
			cs1.m_edge[i] = t4[i] + (4*(i/4));
		}
		u = cube_state_to_lrfb (cs1);
		printf ("  2nd: %10u\n", u);
		u = 27326*40320 + 13837;
		printf ("rep for lr 15,4 fb 14,9 is %u\n", lrfb_get_edge_rep (u));
	}
	UINT count = 0;
	UINT repcount = 0;
	UINT repcount2 = 0;
	const UINT n = 40320u*40320u;
	for (u1 = 0; u1 < n; ++u1) {
		if (u1 % 1000 == 0) {
#ifdef USE_TITLE_BAR
			if (u1 == 1000 || u1 == 10000 || u1 == 100000 || u1 == 1000000 || u1 % 10000000 == 0) {
				char str[64];
				sprintf (&str[0], "title lrcheck %u %u", u1, repcount);
				system (&str[0]);
			}
#endif
			if (repcount == 44100 && u1 < 200000000) {
				u1 = 40320u*20160u;
			}
			if (repcount == 88200) {
				break;
			}
		}
		UINT uH = u1 / 40320;
		UINT uL = u1 % 40320;
		if (parity_perm8_table[uH] != parity_perm8_table[uL]) {
			continue;
		}
		UINT myrep = lrfb_get_edge_rep (u1);
		if (myrep == u1) {
			add_to_stage4_edge_table (myrep, repcount++);
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("lrfb check representative count is %u\n", repcount);
#endif
}

void
stage4_edge_table_init ()
{
	UINT i;
	for (i = 0; i < N_STAGE4_EDGE_HASH_TABLE; ++i) {
		stage4_edge_hash_table_val[i] = 40320u*40320u;	//an "invalid" value
		stage4_edge_hash_table_idx[i] = 88200;	//also "invalid" but shouldn't matter
	}
}

bool
stage4_edge_table_lookup (UINT val, UINT* hash_loc)
{
	UINT hash = val % N_STAGE4_EDGE_HASH_DIVISOR;
	UINT i = hash + 1;
	while (stage4_edge_hash_table_val[i] < 40320u*40320u) {
		if (stage4_edge_hash_table_val[i] == val) {
			*hash_loc = i;
			return true;
		}
		i += hash;
		i %= N_STAGE4_EDGE_HASH_TABLE;
		if (i == 0) {	//relies on table being a prime number in size
			*hash_loc = 0;
			return false;	//not found, table full, return false
		}
	}
	*hash_loc = i;
	return false;	//new position, it was not found in the table
}

void
add_to_stage4_edge_table (UINT val, UINT idx)
{
	UINT hash_idx;
	if (stage4_edge_table_lookup (val, &hash_idx)) {
		printf ("edge hash table: duplicate value!\n");
	} else {
		if (hash_idx == 0) {
			printf ("Stage4 edge hash table full!\n");
			exit (0);
		}
		stage4_edge_hash_table_val[hash_idx] = val;
		stage4_edge_hash_table_idx[hash_idx] = idx;
		stage4_edge_rep_table[idx] = val;
	}
}
#endif

#ifdef STAGE4_INIT
void
stage4_solved_edges ()
{
	int i;
	UINT u1, u2, u3, u4;
	Face t[16];
	UINT count = 0;
	CubeState cs1;
	cs1.init ();
	for (u1 = 0; u1 < 24; ++u1) {	//Alr
		perm_n_unpack (4, u1, &t[0]);
		UINT Alr6 = sqs_perm_to_rep[u1];
		UINT Alr1 = u1/6;
		for (u2 = 0; u2 < 24; ++u2) {	//Afb
			perm_n_unpack (4, u2, &t[12]);
			UINT Afb6 = sqs_perm_to_rep[u2];
			UINT Afb1 = u2/6;
			for (u3 = 0; u3 < 24; ++u3) {	//Blr
				perm_n_unpack (4, u3, &t[4]);
				UINT Blr6 = sqs_perm_to_rep[u3];
				if (Blr6 != Alr6) {
					continue;
				}
				UINT Blr1 = u3/6;
				for (u4 = 0; u4 < 24; ++u4) {	//Bfb
					perm_n_unpack (4, u4, &t[8]);
					UINT Bfb6 = sqs_perm_to_rep[u4];
					if (Bfb6 != Afb6) {
						continue;
					}
					UINT Bfb1 = u4/6;
					for (i = 0; i < 16; ++i) {
						cs1.m_edge[i] = t[i] + (4*(i/4));
					}
					UINT u = cube_state_to_lrfb (cs1);
					stage4_solved_edge_configs[count++] = u;
				}
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("solved edge configurations: %u\n", count);
#endif
}
#endif

#ifdef STAGE4_INIT
void
init_stage4 ()
{
	int i, j;
	UINT u, v, w, u2, sym1;
	CubeStage4 s4a, s4b;
	s4a.init ();
	s4b.init ();
	Face t[8];
	Face t2[8];
	Face t3[8];
	for (u = 0; u < 40320; ++u) {
		perm_to_420[u] = 999;
	}
	for (u = 0; u < 70; ++u) {
		UINT bm = bm4of8[u];
		for (v = 0; v < 6; ++v) {
			perm_n_unpack (8, v, &t[0]);
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i) {
					t2[i] = map96[w][t[i]];
				}
				int f = 0;
				int b = 4;
				for (i = 0; i < 8; ++i) {
					if ((bm & (1 << i)) == 0) {
						t3[i] = t2[b++];
					} else {
						t3[i] = t2[f++];
					}
				}
				u2 = perm_n_pack (8, &t3[0]);
				perm_to_420[u2] = 6*u + v;
			}
		}
	}
	for (u = 0; u < 40320; ++u) {
		if (perm_to_420[u] >= 420) {
			printf ("perm to 420 error %u\n", u);
		}
	}
	stage4_cor_check ();
	for (u = 0; u < N_STAGE4_CORNER_CONFIGS; ++u) {
		s4a.m_corner = u;
		for (sym1 = 0; sym1 < 16; ++sym1) {
			reorient_cubeSTAGE4_slow (s4a, sym1, &s4b);
			reorient_s4cor[u][sym1] = s4b.m_corner;
		}
	}
	s4a.init ();
	for (u = 0; u < N_STAGE4_CENTER_CONFIGS; ++u) {
		s4a.m_centerUD = u;
		for (sym1 = 0; sym1 < 16; ++sym1) {
            reorient_cubeSTAGE4_slow (s4a, sym1, &s4b);
			reorient_s4cen[u][sym1] = s4b.m_centerUD;
		}
	}
	s4a.init ();
	for (u = 0; u < N_STAGE4_EDGE_CONFIGS; ++u) {
		s4a.m_edge = u;
		for (sym1 = 0; sym1 < 16; ++sym1) {
            reorient_cubeSTAGE4_slow (s4a, sym1, &s4b);
			reorient_s4edge[u][sym1] = s4b.m_edge;
		}
	}
	init_move_tablesSTAGE4 ();

	for (u = 0; u < N_STAGE4_EDGE_CONFIGS; ++u) {
		stage4_edge88200_to_edge_sym[u] = 99000;
	}
	UINT count = 0;
	s4a.init ();
	s4b.init ();
	for (u = 0; u < N_STAGE4_EDGE_CONFIGS; ++u) {
		if (stage4_edge88200_to_edge_sym[u] == 99000) {
			stage4_edge88200_to_edge_sym[u] = N_SYM_STAGE4*count++;
			stage4_edge_sym_to_edge88200[stage4_edge88200_to_edge_sym[u]] = u;
			for (sym1 = 1; sym1 < N_SYM_STAGE4; ++sym1) {
				s4a.m_edge = u;
				reorient_cubeSTAGE4 (s4a, sym1, &s4b);
				UINT u2 = s4b.m_edge;
				if (stage4_edge88200_to_edge_sym[u2] == 99000) {

					stage4_edge88200_to_edge_sym[u2] = stage4_edge88200_to_edge_sym[u] + sym1;
				} else {
					if (stage4_edge88200_to_edge_sym[u2]/N_SYM_STAGE4 != stage4_edge88200_to_edge_sym[u]/N_SYM_STAGE4) {
						printf ("sym-coodinate generation inconsistency!\n");
						exit (1);
					}
				}
				if (u2 < u) {
					printf ("sym-coordinate generation inconsistency: u2 < u\n");
				}
				stage4_edge_sym_to_edge88200[stage4_edge88200_to_edge_sym[u] + sym1] = u2;
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("edge group count = %d\n", count);
#endif
}

void
init_move_tablesSTAGE4 ()
{
	int mc;
	UINT u;
	CubeStage4 s4, s4a;
	s4.init ();
	s4a.init ();
	CubeState cs1;
	cs1.init ();
	for (u = 0; u < 40320; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			lrfb_to_cube_state (u, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			UINT u2 = cube_state_to_lrfb (cs1);
			move_table_AedgeSTAGE4[u][mc] = u2 % 40320;
		}
	}
	for (u = 0; u < 40320; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			lrfb_to_cube_state (40320*u, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			UINT u2 = cube_state_to_lrfb (cs1);
			move_table_BedgeSTAGE4[u][mc] = u2 / 40320;
		}
	}
	s4.m_edge = 0;
	s4.m_centerUD = 0;
	for (u = 0; u < N_STAGE4_CORNER_CONFIGS; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			s4.m_corner = u;
			convert_stage4_to_std_cube (s4, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			convert_std_cube_to_stage4 (cs1, &s4a);
			move_table_cornerSTAGE4[u][mc] = s4a.m_corner;
		}
	}
	s4.m_edge = 0;
	s4.m_corner = 0;
	for (u = 0; u < N_STAGE4_CENTER_CONFIGS; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			s4.m_centerUD = u;
			convert_stage4_to_std_cube (s4, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			convert_std_cube_to_stage4 (cs1, &s4a);
			move_table_cenSTAGE4[u][mc] = s4a.m_centerUD;
		}
	}
}

UINT
cor_find96 (UINT u, Bitvec* bv, UINT* mylist)
{
	int mc, i, j, q;
	int h;
	CubeState cs1, cs2, cs3;
	cs1.init ();
	cs2.init ();
	cs3.init ();
	perm_n_unpack (8, u, &cs2.m_cor[0]);
	mylist[0] = u;
	bv->setb (u);
	UINT x = perm_to_420[u];
	UINT samerepcount = 1;
	for (h = 1; h < 96; ++h) {
		UINT u1 = stage4_solved_corner_configs[h];
		perm_n_unpack (8, u1, &cs1.m_cor[0]);
		cs3 = cs1;
		cs3.compose_corner (cs1, cs2);
		UINT u3 = perm_n_pack (8, &cs3.m_cor[0]);
		if (u3 < u) {		//comment out if you won't require this function to be called with the representative value.
			printf ("%u not representative (%u is less)\n", u, u3);
			return 0;
		}
		bool found = false;
		for (j = 0; j < h && !found; ++j) {
			if (u3 == mylist[j]) {
				found = true;
			}
		}
		if (found) {
			printf ("duplicate element in mylist\n");
		}
		if (! bv->tstb (u3)) {
			mylist[h] = u3;
			bv->setb (u3);
			UINT x3 = perm_to_420[u3];
			if (x3 != x) {
				printf ("different representative: %u->%u %u->%u\n", u, x, u3, x3);
			} else {
				++samerepcount;
			}
		} else {
			//This version should not reach same element more than once
			printf ("cor_find96 inconsistency!\n");
			exit (1);
		}
	}
	if (samerepcount != 96) {
		printf ("%u same reps count %u\n", u, samerepcount);
	}
	return 96;
}

void
stage4_cor_check ()
{
	UINT c1, c2, u1, u2, v;
	UINT idx = 0;
	int i;
	UINT mylist[96];
	Face t1[8], t2[8];
	Face tx[8];
	CubeState cs1, cs2;
	cs1.init ();
	cs2.init ();

#ifdef OUTPUT_DIAGNOSTICS
	printf ("stage4 corner check\n");
#endif
	int solved_count = 0;
	for (u1 = 0; u1 < 24; ++u1) {
		for (u2 = 0; u2 < 24; ++u2) {
			if (sqs_perm_to_rep[u1] != sqs_perm_to_rep[u2]) {
				continue;
			}
			perm_n_unpack (4, u1, &t1[0]);
			perm_n_unpack (4, u2, &t1[4]);
			for (i = 4; i < 8; ++i) {
				t1[i] += 4;
			}
			stage4_solved_corner_configs[solved_count++] = perm_n_pack (8, &t1[0]);
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("solved corner count %d\n", solved_count);
	Bitvec cor_bm (40320u);
	UINT count = 0;
	UINT repcount = 0;
	UINT ycount = 0;
	const UINT n = 40320u;
	for (u1 = 0; u1 < n; ++u1) {
#ifdef USE_TITLE_BAR
		if (u1 % 1000 == 0) {
			char str[64];
			sprintf (&str[0], "title lrcheck %u %u", u1, repcount);
			system (&str[0]);
		}
#endif
		if (! cor_bm.tstb (u1)) {
			count += cor_find96 (u1, &cor_bm, &mylist[0]);
			++repcount;
			UINT x = perm_to_420[u1];
			UINT xcount = 0;
			for (c1 = 0; c1 < u1; ++c1) {
				if (perm_to_420[c1] == x) {
					++xcount;
					printf ("%u not a rep (%u)\n", u1, c1);
				}
			}
			if (xcount == 0) {
				++ycount;
			}
		}
	}
	printf ("lrfb check count is %u\n", count);
	printf ("lrfb check representative count is %u\n", repcount);
	printf ("'ycount' is %u\n", ycount);
#endif
}

void
check_stage4 ()
{
	UINT u;
	CubeStage4 s4, s4a;
	CubeState cs1;
	s4.init ();
	s4a.init ();
	cs1.init ();
	for (u = 0; u < N_STAGE4; ++u) {
#ifdef USE_TITLE_BAR
			if (u % 100000000 == 0) {
				char str[64];
				sprintf (&str[0], "title checkSTAGE4 %u", u);
				system (&str[0]);
			}
#endif
		s4.m_edge = u % N_STAGE4_EDGE_CONFIGS;
		s4.m_centerUD = (u / N_STAGE4_EDGE_CONFIGS) % N_STAGE4_CENTER_CONFIGS;
		s4.m_corner = u / (N_STAGE4_EDGE_CONFIGS * N_STAGE4_CENTER_CONFIGS);
		convert_stage4_to_std_cube (s4, &cs1);
		convert_std_cube_to_stage4 (cs1, &s4a);
		if (! s4.compare (s4a)) {
			printf ("conversion error for %u\n", u);
		}
	}
}
#endif

void
init_edgemapSQS ()
{
	UINT u, sym;
	CubeSqsCoord cube1, cube2;
	CubeSymSqsCoord cube3;
	cube1.init ();
	cube3.init ();
	stab1.set_bit (0);

	for (u = 0; u < N_SQS_EDGE_PERM; ++u) {
		sqs_ep96x96x96_to_edge[u] = 65000;
		sqs_ep96x96x96_to_sym[u] = 99;
	}
	UINT count = 0;
	for (u = 0; u < N_SQS_EDGE_PERM; ++u) {
		if (sqs_ep96x96x96_to_edge[u] == 65000) {
			sqs_ep96x96x96_to_edge[u] = count++;
			sqs_ep96x96x96_to_sym[u] = 0;
			sqs_edge_to_ep96x96x96[N_SYMX*sqs_ep96x96x96_to_edge[u]] = u;
			for (sym = 1; sym < N_SYMX; ++sym) {
				cube1.m_ep96x96x96 = u;
				reorient_cubeSQS (cube1, sym, &cube2);
				UINT u2 = cube2.m_ep96x96x96;
				if (sqs_ep96x96x96_to_edge[u2] == 65000) {
					sqs_ep96x96x96_to_edge[u2] = sqs_ep96x96x96_to_edge[u];
					sqs_ep96x96x96_to_sym[u2] = sym;
				} else {
					if (sqs_ep96x96x96_to_edge[u2] != sqs_ep96x96x96_to_edge[u]) {
						printf ("sym-coodinate generation inconsistency!\n");
						exit (1);
					}
				}
				if (u2 < u) {
					printf ("sym-coordinate generation inconsistency: u2 < u\n");
				}
				sqs_edge_to_ep96x96x96[N_SYMX*sqs_ep96x96x96_to_edge[u] + sym] = u2;
			}
		}
	}
#ifdef OUTPUT_DIAGNOSTICS
	printf ("edge group count = %d\n", count);
#endif
	for (u = 0; u < N_SYMCOUNT; ++u) {
		UINT sym;
		Stab sym_bm;
		sym_bm.set_bit (0);
		int sym_count = 1;

		UINT ep1 = sqs_edge_to_ep96x96x96[N_SYMX*u];
		cube1.m_ep96x96x96 = ep1;
		for (sym = 1; sym < N_SYMX; ++sym) {
			UINT cp2, ep2;
			reorient_cubeSQS (cube1, sym, &cube2);
			ep2 = cube2.m_ep96x96x96;
			if (ep2 < ep1) {
				printf ("edgemap gen error!\n");
				return; // false;
			}
			if (ep2 == ep1) {
				sym_bm.set_bit (sym);
				++sym_count;
			}
		}
		sqs_edge_stab[u] = sym_bm;
		sqs_edge_mult[u] = N_SYMX / sym_count;
	}
#ifdef WHOLE_CUBE
	UINT cori;
	int i;
	Face t[8];
	for (u = 0; u < N_CORNER_PERM; ++u) {
		if (u == 8064) {
			int xswer = 1234;
		}
		perm_n_unpack (8, u, &t[0]);	//t[] contains "what's at" info
		UINT bm = 0;
		for (i = 0; i < 8; ++i) {
			if (evenodd[t[i]] != 0) {	//perhaps we should unroll this loop... (but still need to loop to init t2[]
				bm |= (1 << i);
			}
		}
		cornerperm_to_evenoddbm[u] = bm;	//tells which positions contain the "odd" cubies
	}
	for (u = 0; u < 256; ++u) {
		for (cori = 0; cori < N_CORNER_ORIENT; ++cori) {
			luf_on_cornerori[cori][u] = 9999;
		}
	}
	for (u = 0; u < N_CORNER_PERM; ++u) {
		UINT bm = cornerperm_to_evenoddbm[u];
		for (cori = 0; cori < N_CORNER_ORIENT; ++cori) {
			if (luf_on_cornerori[cori][bm] == 9999) {
				luf_on_cornerori[cori][bm] = compute_luf_on_cornerori_slow (cori, u);
			}
		}
	}
	for (u = 0; u < N_CORNER_ORIENT; ++u) {
		esym_on_cornerori[u][0] = u;
	}
	for (cori = 0; cori < N_CORNER_ORIENT; ++cori) {
		for (sym = 1; sym < N_ESYM; ++sym) {
			csym_calc (cori, sym, &esym_on_cornerori[cori][sym]);
		}
	}
#endif
#ifdef OUTPUT_DIAGNOSTICS
	UINT u1, u2;
	UINT good_count = 0;
	UINT bad_count = 0;
	for (u = 0; u < N_SQS_EDGE_PERM; ++u) {
		UINT epsym = N_SYMX * sqs_ep96x96x96_to_edge[u] + sqs_ep96x96x96_to_sym[u];
		UINT ep2 = sqs_edge_to_ep96x96x96[epsym];
		if (ep2 == u) {
			++good_count;
		} else {
			++bad_count;
		}
	}
	printf ("conversion test: good %10u  bad %10u\n", good_count, bad_count);
#endif
}

void
check_rep (const CubeSymSqsCoord& cube1)
{
	int sym1;
	CubeSqsCoord rep, cube2, rep2;
	rep.m_ep96x96x96 = sqs_edge_to_ep96x96x96[cube1.m_ep_sym];
	rep.m_cp96 = cube1.m_cp96;
	rep.m_cen12x12x12 = cube1.m_cen12x12x12;
	rep.m_distance = cube1.m_distance;
	for (sym1 = 1; sym1 < N_SYMX; ++sym1) {
		cube2.init ();
		reorient_cubeSQS (rep, sym1, &cube2);
		cube2.get_representative (&rep2);
		if (! rep.compare (rep2)) {
			printf ("Representative inconsistency\n");
		}
	}
}

void
init_invsym ()
{
	Face sym_array[N_CUBESYM][6];
	Face temp_array[6];
	int i, k, pack;
	Face t;

	perm_n_init(6, &sym_array[0][0]);
	perm_n_init(6, &sym_array[1][0]);

	t = sym_array[1][FaceL];
	sym_array[1][FaceL] = sym_array[1][FaceR];
	sym_array[1][FaceR] = t;

	perm_n_init(6, &sym_array[2][0]);
	four_cycle (&sym_array[2][0], FaceF, FaceL, FaceB, FaceR);

	perm_n_compose (6, &sym_array[2][0], &sym_array[1][0], &sym_array[3][0]);

	for (i = 4; i < 8; i += 2) {
		perm_n_compose (6, &sym_array[i-2][0], &sym_array[2][0], &sym_array[i][0]);
		perm_n_compose (6, &sym_array[i][0], &sym_array[1][0], &sym_array[i+1][0]);
	}

	perm_n_init(6, &sym_array[8][0]);

	t = sym_array[8][FaceU];
	sym_array[8][FaceU] = sym_array[8][FaceD];
	sym_array[8][FaceD] = t;
	t = sym_array[8][FaceR];
	sym_array[8][FaceR] = sym_array[8][FaceL];
	sym_array[8][FaceL] = t;

	for (i = 9; i < 16; ++i) {
		perm_n_compose (6, &sym_array[8][0], &sym_array[i - 8][0], &sym_array[i][0]);
	}

	perm_n_init (6, &sym_array[16][0]);

	three_cycle (&sym_array[16][0], FaceU, FaceL, FaceF);
	three_cycle (&sym_array[16][0], FaceD, FaceR, FaceB);

	for (i = 17; i < 48; ++i) {
		perm_n_compose (6, &sym_array[16][0], &sym_array[i-16][0], &sym_array[i][0]);
	}

	for (i = 0; i < N_CUBESYM; ++i) {
		for (k = 0; k < N_CUBESYM; ++k) {
			perm_n_compose (6, &sym_array[i][0], &sym_array[k][0], &temp_array[0]);
			pack = perm_n_pack (6, &temp_array[0]);
			if (pack == 0) {
				cube_sym_inv[i] = k;
				break;
			}
		}
		if (k == N_CUBESYM) {
			printf ("init_invsym : inverse not found\n");
		}
	}
}

void
rotate_sliceEDGE (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	//Caller must initialize result cube m_edge[] array!
	int i;
	int mc3 = move_code/3;
	int movdir = move_code % 3;
	int mcx = 3*(mc3/2);
	if ((mc3 & 0x1) != 0) {	//slice move?
		mcx += 2;
	}
	int fidx = rotateEDGE_fidx[3*mcx + movdir];
	int tidx = rotateEDGE_tidx[3*mcx + movdir];
	for (i = 0; i < 4; ++i) {
		result_cube->m_edge[rotateEDGE_ft[tidx + i]] = init_cube.m_edge[rotateEDGE_ft[fidx + i]];
	}
	if ((mc3 & 0x1) == 0) {	//face move? have a 2nd set of edges to cycle
		fidx = rotateEDGE_fidx[3*(mcx+1) + movdir];
		tidx = rotateEDGE_tidx[3*(mcx+1) + movdir];
		for (i = 0; i < 4; ++i) {
			result_cube->m_edge[rotateEDGE_ft[tidx + i]] = init_cube.m_edge[rotateEDGE_ft[fidx + i]];
		}
	}
}

void
rotate_sliceCORNER (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	//Caller must initialize result cube m_cor[] array!
	int i;
	if (move_code % 6 >= 3) {
		return;		//inner slice turn, no corners affected
	}
	int mc6 = move_code/6;
	int mc = 3*mc6 + move_code % 3;
	int fidx = rotateCOR_fidx[mc];
	int tidx = rotateCOR_tidx[mc];
	if (mc % 3 != 2) {	//avoid doing "if" inside loop, for speed
		for (i = 0; i < 4; ++i) {
			Face tmpface = init_cube.m_cor[rotateCOR_ft[fidx + i]];
			if (mc >= 6) {	//L,R,F,B face turns
				Face new_ori = (tmpface >> 3) + rotateCOR_ori[i];
				new_ori %= 3;
				tmpface = (tmpface & 0x7) + (new_ori << 3);
			}
			result_cube->m_cor[rotateCOR_ft[tidx + i]] = tmpface;
		}
	} else {
		for (i = 0; i < 4; ++i) {
			result_cube->m_cor[rotateCOR_ft[tidx + i]] = init_cube.m_cor[rotateCOR_ft[fidx + i]];
		}
	}
}

void
rotate_sliceCENTER (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	int mc3 = move_code/3;
	int movdir = move_code % 3;
	int mcx = 3*(mc3/2) + (mc3 & 0x1);
	int fidx = rotateCEN_fidx[3*mcx + movdir];
	int tidx = rotateCEN_tidx[3*mcx + movdir];
	for (i = 0; i < 4; ++i) {
		result_cube->m_cen[rotateCEN_ft[tidx + i]] = init_cube.m_cen[rotateCEN_ft[fidx + i]];
	}
	if ((mc3 & 0x1) == 1) {	//slice move? have a 2nd set of centers to cycle
		fidx = rotateCEN_fidx[3*(mcx+1) + movdir];
		tidx = rotateCEN_tidx[3*(mcx+1) + movdir];
		for (i = 0; i < 4; ++i) {
			result_cube->m_cen[rotateCEN_ft[tidx + i]] = init_cube.m_cen[rotateCEN_ft[fidx + i]];
		}
	}
}

void
convert_stage1_to_std_cube (const CubeStage1& init_cube, CubeState* result_cube)
{
#ifdef ELOC_INIT
	int i;
	UINT ebm = eloc2ebm[init_cube.m_edge_ud_combo8];
	int lrfb = 0;
	int ud = 16;
	for (i = 0; i < 24; ++i) {
		if ((ebm & (1 << i)) == 0) {
			result_cube->m_edge[i] = lrfb++;
		} else {
			result_cube->m_edge[i] = ud++;
		}
		result_cube->m_cen[i] = i/4;
	}
	UINT orientc = init_cube.m_co;
	UINT orientcmod3 = 0;
	for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
		Face fo = orientc % 3;
		result_cube->m_cor[i] = i + (fo << 3);
		orientcmod3 += fo;
		orientc /= 3;
	}
	result_cube->m_cor[7] = 7 + (((24 - orientcmod3) % 3) << 3);
#endif
}

void
convert_std_cube_to_stage1 (const CubeState& init_cube, CubeStage1* result_cube)
{
#ifdef ELOC_INIT
	int i;
	UINT ebm = 0;
	for (i = 0; i < 24; ++i) {
		if (init_cube.m_edge[i] >= 16) {
			ebm |= (1 << i);
		}
	}
	result_cube->m_edge_ud_combo8 = ebm2eloc[ebm];
	UINT orientc = 0;
	for (i = 0; i < 7; ++i) {	//don't want 8th edge orientation
		orientc = 3*orientc + (init_cube.m_cor[i] >> 3);
	}
	result_cube->m_co = orientc;
#endif
}

#ifdef STAGE2_INIT
void
convert_stage2_to_std_cube (const CubeStage2& init_cube, CubeState* result_cube)
{
	int i;
	Face t6[4];
	UINT cenbm = eloc2ebm[init_cube.m_centerFB/70];
	UINT cenbm4of8 = bm4of8[init_cube.m_centerFB % 70];
	int udlr = 0;
	int pos4of8 = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = udlr++/4;
		} else {
			if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
				result_cube->m_cen[i] = 5;
			} else {
				result_cube->m_cen[i] = 4;
			}
		}
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = i;
	}
	UINT edgeFbm = bm4of8[init_cube.m_edge / 6];
	perm_n_unpack (4, init_cube.m_edge % 6, &t6[0]);
	for (i = 0; i < 16; ++i) {
		result_cube->m_edge[i] = i;
	}
	int f = 16;
	int b = 0;
	for (i = 0; i < 8; ++i) {
		if ((edgeFbm & (1 << i)) == 0) {
			result_cube->m_edge[16 + i] = 20 + t6[b++];
		} else {
			result_cube->m_edge[16 + i] = f++;
		}
	}
}

void
convert_std_cube_to_stage2 (const CubeState& init_cube, CubeStage2* result_cube)
{
	int i;
	UINT cenbm = 0;
	UINT cenbm4of8 = 0;
	int j = 0;
	for (i = 0; i < 24; ++i) {
		if (init_cube.m_cen[i] >= 4) {
			cenbm |= (1 << i);
			if (init_cube.m_cen[i] == 4) {
				cenbm4of8 |= (1 << j);
			}
			++j;
		}
	}
	result_cube->m_centerFB = 70*ebm2eloc[cenbm] + bm4of8_to_70[cenbm4of8];
	UINT u = perm_n_pack (8, &init_cube.m_edge[16]);
	result_cube->m_edge = perm_to_420[u];
}
#endif

#ifdef STAGE3_INIT
void
convert_stage3_to_std_cube (const CubeStage3& init_cube, CubeState* result_cube)
{
	int i;
	Face t6[4];
	UINT cenbm = eloc2e16bm[init_cube.m_centerLR/70];
	UINT cenbm4of8 = bm4of8[init_cube.m_centerLR % 70];
	int ud = 0;
	int pos4of8 = 0;
	for (i = 0; i < 16; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = ud++/4;
		} else {
			if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
				result_cube->m_cen[i] = 3;
			} else {
				result_cube->m_cen[i] = 2;
			}
		}
	}
	for (i = 16; i < 24; ++i) {
		result_cube->m_cen[i] = i/4;
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = i;
	}
	UINT edge_bm = eloc2e16bm[init_cube.m_edge];
	UINT e0 = 0;
	UINT e1 = 4;
	for (i = 0; i < 16; ++i) {
		if ((edge_bm & (1 << i)) != 0) {
			result_cube->m_edge[i] = e0++;
			if (e0 == 4) {
				e0 = 12;		//skip numbers 4..11; those are used for e1
			}
		} else {
			result_cube->m_edge[i] = e1++;
		}
	}
	for (i = 16; i < 24; ++i) {
		result_cube->m_edge[i] = i;
	}
}

void
convert_std_cube_to_stage3 (const CubeState& init_cube, CubeStage3* result_cube)
{
	int i;
	UINT cenbm = 0;
	UINT cenbm4of8 = 0;
	int j = 0;
	for (i = 0; i < 16; ++i) {
		if (init_cube.m_cen[i] >= 4) {
			printf ("error: cube state not a stage3 position\n");
			exit (1);
		}
		if (init_cube.m_cen[i] >= 2) {
			cenbm |= (1 << i);
			if (init_cube.m_cen[i] == 2) {
				cenbm4of8 |= (1 << j);
			}
			++j;
		}
	}
	result_cube->m_centerLR = 70*e16bm2eloc[cenbm] + bm4of8_to_70[cenbm4of8];
	UINT edge_bm = 0;
	for (i = 0; i < 16; ++i) {
		if (init_cube.m_edge[i] >= 16) {
			printf ("error: cube state not a stage3 position\n");
			exit (1);
		}
		if (init_cube.m_edge[i] < 4 || init_cube.m_edge[i] >= 12) {
			edge_bm |= (1 << i);
		}
	}
	result_cube->m_edge = e16bm2eloc[edge_bm];
}
#endif

void
convert_stage4_raw_edge_to_std_cube (UINT edge, CubeState* result_cube)
{
	int i;
	Face t1[8], t2[8];
	UINT edgeB = edge/40320;
	UINT edgeA = edge % 40320;
	result_cube->init ();
	perm_n_unpack (8, edgeA, &t1[0]);
	perm_n_unpack (8, edgeB, &t2[0]);
	for (i = 0; i < 8; ++i) {
		int i2 = i;
		if (i2 >= 4) {
			i2 += 8;
		}
		if (t1[i] >= 4) {
			result_cube->m_edge[i2] = t1[i] + 8;
		} else {
			result_cube->m_edge[i2] = t1[i];
		}
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_edge[i + 4] = t2[i] + 4;
	}
}

UINT
convert_std_cube_to_stage4_raw_edge (const CubeState& init_cube)
{
	int i;
	Face t1[8], t2[8];
	Face f;
	for (i = 0; i < 8; ++i) {
		int i2 = i;
		if (i2 >= 4) {
			i2 += 8;
		}
		f = init_cube.m_edge[i2];
		if (f >= 12) {
			t1[i] = init_cube.m_edge[i2] - 8; 
		} else {
			if (f >= 4) {
				printf ("bad cube\n");
				exit (1);
			}
			t1[i] = f;
		}
	}
	for (i = 0; i < 8; ++i) {
		f = init_cube.m_edge[i + 4];
		if (f < 4 || f >= 12) {
			printf ("bad cube\n");
		}
		t2[i] = f - 4;
	}
	UINT edgeA = perm_n_pack (8, &t1[0]);
	UINT edgeB = perm_n_pack (8, &t2[0]);
	return 40320*edgeB + edgeA;
}

void
convert_stage4_to_std_cube (const CubeStage4& init_cube, CubeState* result_cube)
{
#ifdef STAGE4_INIT
	int i;
	Face t6[4], t8[8];
	//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
	//But the do_move function for std_cube assumes "standard" mapping.
	//Therefore the m_cor array must be converted accordingly using this conversion array.
	static Face sqs_to_std[8] = { 0, 2, 5, 7, 1, 3, 4, 6 };

	UINT edge = stage4_edge_rep_table[init_cube.m_edge];
	lrfb_to_cube_state (edge, result_cube);	//note: initializes result_cube! so we do this first
	UINT cor_bm = bm4of8[init_cube.m_corner / 6];
	perm_n_unpack (4, init_cube.m_corner % 6, &t6[0]);
	int a = 0;
	int b = 0;
	for (i = 0; i < 8; ++i) {
		if ((cor_bm & (1 << i)) == 0) {
			t8[i] = 4 + t6[b++];
		} else {
			t8[i] = a++;
		}
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[sqs_to_std[i]] = sqs_to_std[t8[i]];
	}
	UINT cenbm = bm4of8[init_cube.m_centerUD];
	for (i = 0; i < 8; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = 1;
		} else {
			result_cube->m_cen[i] = 0;
		}
	}
	for (i = 8; i < 24; ++i) {
		result_cube->m_cen[i] = i/4;
	}
#else
	printf ("Stage 4 not set up.\n");
	exit (1);
#endif
}

void
convert_std_cube_to_stage4 (const CubeState& init_cube, CubeStage4* result_cube)
{
#ifdef STAGE4_INIT
	int i;
	Face t6[8];
	//Note: for corners, use of perm_to_420 array requires "squares" style mapping.
	//But the do_move function for std_cube assumes "standard" mapping.
	//Therefore the m_cor array must be converted accordingly using this conversion array.
	static Face std_to_sqs[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	UINT edge = cube_state_to_lrfb (init_cube);
	UINT edgerep = lrfb_get_edge_rep (edge);
	UINT hash_idx;
	if (stage4_edge_table_lookup (edgerep, &hash_idx)) {
		result_cube->m_edge = stage4_edge_hash_table_idx[hash_idx];
	} else {
		printf ("stage4 edge value error\n");
		exit (1);
	}
	for (i = 0; i < 8; ++i) {
		t6[std_to_sqs[i]] = std_to_sqs[init_cube.m_cor[i]];
	}
	UINT u = perm_n_pack (8, &t6[0]);
	result_cube->m_corner = perm_to_420[u];
	UINT cenbm4of8 = 0;
	int j = 0;
	for (i = 0; i < 8; ++i) {
		if (init_cube.m_cen[i] >= 2) {
			printf ("error: cube state not a stage4 position\n");
			exit (1);
		}
		if (init_cube.m_cen[i] == 0) {
			cenbm4of8 |= (1 << i);
		}
	}
	result_cube->m_centerUD = bm4of8_to_70[cenbm4of8];
#else
	printf ("Stage 4 not set up.\n");
	exit (1);
#endif
}

void
convert_std_cube_to_squares (const CubeState& init_cube, CubeSqsCoord* result_cube)
{
	int i;
	CubeState cs_sqs;
	//We must convert between "squares"-style cubie numbering and the "standard"-style
	//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
	static Face std_to_sqs_cor[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	static Face std_to_sqs_cen[24] = {
		0,  3,  1,  2,  5,  6,  4,  7,
		8, 11,  9, 10, 13, 14, 12, 15,
	   16, 19, 17, 18, 21, 22, 20, 23
	};
	cs_sqs = init_cube;
	for (i = 0; i < 8; ++i) {
		cs_sqs.m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[init_cube.m_cor[i]];
	}
	for (i = 0; i < 24; ++i) {
		cs_sqs.m_cen[std_to_sqs_cen[i]] = std_to_sqs_cen[4*init_cube.m_cen[i]]/4;
	}
	pack_cubeSQS (cs_sqs, result_cube);
}

void
pack_cubeSQS (const CubeState& cube1, CubeSqsCoord* result_cube)
{
	int i;

	result_cube->m_distance = cube1.m_distance;
	UINT ep1 = perm_n_pack (4, &cube1.m_edge[0]);
	UINT ep2 = perm_n_pack (4, &cube1.m_edge[8]);
	UINT ep3 = perm_n_pack (4, &cube1.m_edge[16]);
	result_cube->m_ep96x96x96 = 96*96*(4*ep3 + (cube1.m_edge[20] - 20)) + 96*(4*ep2 + (cube1.m_edge[12] - 12)) +
		4*ep1 + (cube1.m_edge[4] - 4);
	result_cube->m_cp96 = 4*perm_n_pack (4, &cube1.m_cor[0]) + (cube1.m_cor[4] - 4);
	result_cube->m_cen12x12x12 = squares_pack_centers (&cube1.m_cen[0]);
}

void
unpack_cubeSQS (const CubeSqsCoord& cube1, CubeState* result_cube)
{
	int i;
	Face t[24];

	result_cube->m_distance = cube1.m_distance;
	UINT ep1 = cube1.m_ep96x96x96 % 96;
	UINT ep2 = (cube1.m_ep96x96x96/96) % 96;
	UINT ep3 = cube1.m_ep96x96x96 / (96*96);
	UINT rep = sqs_perm_to_rep[ep1/4];
	perm_n_unpack (4, ep1/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep1 % 4], &t[4]);
	rep = sqs_perm_to_rep[ep2/4];
	perm_n_unpack (4, ep2/4, &t[8]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep2 % 4], &t[12]);
	rep = sqs_perm_to_rep[ep3/4];
	perm_n_unpack (4, ep3/4, &t[16]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep3 % 4], &t[20]);
	for (i = 0; i < 24; ++i) {
		result_cube->m_edge[i] = 4*(i/4) + t[i];
	}
	rep = sqs_perm_to_rep[cube1.m_cp96/4];
	perm_n_unpack (4, cube1.m_cp96/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][cube1.m_cp96 % 4], &t[4]);
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = 4*(i/4) + t[i];
	}
	UINT cen1 = cube1.m_cen12x12x12 % 12;
	UINT cen2 = (cube1.m_cen12x12x12/12) % 12;
	UINT cen3 = cube1.m_cen12x12x12/(12*12);
	squares_unpack_centers (cen1, cen2, cen3, &(result_cube->m_cen[0]));
}

void
reorient_cubeSQS (const CubeSqsCoord& init_cube, int sym, CubeSqsCoord* result_cube)
{
	//sym is a symmetry/antisymmetry code (0..95) for the 48 symmetries of the cube, and inverses.
	UINT u;
	int i;
	Face t[24];
	UINT ep = init_cube.m_ep96x96x96;
	UINT ep1 = ep % 96;
	UINT ep2 = (ep/96) % 96;
	UINT ep3 = ep/(96*96);
	UINT rep = sqs_perm_to_rep[ep1/4];
	perm_n_unpack (4, ep1/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep1 % 4], &t[4]);
	rep = sqs_perm_to_rep[ep2/4];
	perm_n_unpack (4, ep2/4, &t[8]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep2 % 4], &t[12]);
	rep = sqs_perm_to_rep[ep3/4];
	perm_n_unpack (4, ep3/4, &t[16]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep3 % 4], &t[20]);
	CubeState cube1;
	cube1.init ();
	for (i = 0; i < 24; ++i) {
		cube1.m_edge[i] = 4*(i/4) + t[i];
	}
	rep = sqs_perm_to_rep[init_cube.m_cp96/4];
	perm_n_unpack (4, init_cube.m_cp96/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][init_cube.m_cp96 % 4], &t[4]);
	for (i = 0; i < 8; ++i) {
		cube1.m_cor[i] = 4*(i/4) + t[i];
	}
	UINT cen1 = init_cube.m_cen12x12x12 % 12;
	UINT cen2 = (init_cube.m_cen12x12x12/12) % 12;
	UINT cen3 = init_cube.m_cen12x12x12/(12*12);
	squares_unpack_centers (cen1, cen2, cen3, &cube1.m_cen[0]);
	CubeState cube2;
	UINT sym1 = (sym/2) % 4;
	UINT sym2 = (sym/8) % 2;
	UINT sym3 = (sym/16) % 3;
	bool inverse = (sym >= N_CUBESYM);
	for (u = 0; u < sym3; ++u) {
		reorient_cube_hSQS (cube1, &cube2);
		reorient_cube_hSQS (cube2, &cube1);
		reorient_cube_hSQS (cube1, &cube2);
		reorient_cube_vSQS (cube2, &cube1);
	}
	//++ check code: remove for better performance
	switch (sym3) {
	case 0:
		squares_unpack_centers (cen1, cen2, cen3, &t[0]);
		break;
	case 1:
		squares_unpack_centers (cen2, cen3, cen1, &t[0]);
		break;
	case 2:
		squares_unpack_centers (cen3, cen1, cen2, &t[0]);
		break;
	}
	for (i = 0; i < 24; ++i) {
		if (t[i] != cube1.m_cen[i]) {
			printf ("cube centers mismatch!\n");
		}
	}
	//-- end check code
	if (sym2 != 0) {
		reorient_cube_hSQS (cube1, &cube2);
		reorient_cube_hSQS (cube2, &cube1);
		reorient_cube_vSQS (cube1, &cube2);
		reorient_cube_vSQS (cube2, &cube1);
	}
	for (u = 0; u < sym1; ++u) {
		reorient_cube_hSQS (cube1, &cube2);
		cube1 = cube2;
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlSQS (cube1, &cube2);
		cube1 = cube2;
	}
	if (inverse) {
		inverse_cubeSQS (cube1, &cube2);
		cube1 = cube2;
	}
	pack_cubeSQS (cube1, result_cube);
	result_cube->m_distance = 255;
	if (! result_cube->check ()) {
		printf ("inconsistent CubeSqsCoord state.\n");
		exit (1);
	}
}

void
pack_cubeSCSQS (const CubeState& cube1, CubeSuperSqsCoord* result_cube)
{
	int i;

	result_cube->m_distance = cube1.m_distance;
	UINT ep1 = perm_n_pack (4, &cube1.m_edge[0]);
	UINT ep2 = perm_n_pack (4, &cube1.m_edge[8]);
	UINT ep3 = perm_n_pack (4, &cube1.m_edge[16]);
	result_cube->m_ep96x96x96 = 96*96*(4*ep3 + (cube1.m_edge[20] - 20)) + 96*(4*ep2 + (cube1.m_edge[12] - 12)) +
		4*ep1 + (cube1.m_edge[4] - 4);
	result_cube->m_cp96 = 4*perm_n_pack (4, &cube1.m_cor[0]) + (cube1.m_cor[4] - 4);
	UINT cen1 = perm_n_pack (4, &cube1.m_cen[0]);
	UINT cen2 = perm_n_pack (4, &cube1.m_cen[8]);
	UINT cen3 = perm_n_pack (4, &cube1.m_cen[16]);
	result_cube->m_cen96x96x96 = 96*96*(4*cen3 + (cube1.m_cen[20] - 20)) + 96*(4*cen2 + (cube1.m_cen[12] - 12)) +
		4*cen1 + (cube1.m_cen[4] - 4);
}

void
unpack_cubeSCSQS (const CubeSuperSqsCoord& cube1, CubeState* result_cube)
{
	int i;
	Face t[24];

	result_cube->m_distance = cube1.m_distance;
	UINT ep1 = cube1.m_ep96x96x96 % 96;
	UINT ep2 = (cube1.m_ep96x96x96/96) % 96;
	UINT ep3 = cube1.m_ep96x96x96 / (96*96);
	UINT rep = sqs_perm_to_rep[ep1/4];
	perm_n_unpack (4, ep1/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep1 % 4], &t[4]);
	rep = sqs_perm_to_rep[ep2/4];
	perm_n_unpack (4, ep2/4, &t[8]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep2 % 4], &t[12]);
	rep = sqs_perm_to_rep[ep3/4];
	perm_n_unpack (4, ep3/4, &t[16]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][ep3 % 4], &t[20]);
	for (i = 0; i < 24; ++i) {
		result_cube->m_edge[i] = 4*(i/4) + t[i];
	}
	rep = sqs_perm_to_rep[cube1.m_cp96/4];
	perm_n_unpack (4, cube1.m_cp96/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][cube1.m_cp96 % 4], &t[4]);
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = 4*(i/4) + t[i];
	}
	UINT cen1 = cube1.m_cen96x96x96 % 96;
	UINT cen2 = (cube1.m_cen96x96x96/96) % 96;
	UINT cen3 = cube1.m_cen96x96x96/(96*96);
	rep = sqs_perm_to_rep[cen1/4];
	perm_n_unpack (4, cen1/4, &t[0]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][cen1 % 4], &t[4]);
	rep = sqs_perm_to_rep[cen2/4];
	perm_n_unpack (4, cen2/4, &t[8]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][cen2 % 4], &t[12]);
	rep = sqs_perm_to_rep[cen3/4];
	perm_n_unpack (4, cen3/4, &t[16]);
	perm_n_unpack (4, sqs_rep_to_perm[rep][cen3 % 4], &t[20]);
	for (i = 0; i < 24; ++i) {
		result_cube->m_cen[i] = 4*(i/4) + t[i];
	}
}

void
reorient_cubeSCSQS (const CubeSuperSqsCoord& init_cube, int sym, CubeSuperSqsCoord* result_cube)
{
	//sym is a symmetry/antisymmetry code (0..95) for the 48 symmetries of the cube, and inverses.
	UINT u;
	int i;
	Face t[24];
	CubeState cube1;
	cube1.init_super ();
	unpack_cubeSCSQS (init_cube, &cube1);
	CubeState cube2;
	UINT sym1 = (sym/2) % 4;
	UINT sym2 = (sym/8) % 2;
	UINT sym3 = (sym/16) % 3;
	bool inverse = (sym >= N_CUBESYM);
	for (u = 0; u < sym3; ++u) {
		reorient_cube_hSCSQS (cube1, &cube2);
		reorient_cube_hSCSQS (cube2, &cube1);
		reorient_cube_hSCSQS (cube1, &cube2);
		reorient_cube_vSCSQS (cube2, &cube1);
	}
	if (sym2 != 0) {
		reorient_cube_hSCSQS (cube1, &cube2);
		reorient_cube_hSCSQS (cube2, &cube1);
		reorient_cube_vSCSQS (cube1, &cube2);
		reorient_cube_vSCSQS (cube2, &cube1);
	}
	for (u = 0; u < sym1; ++u) {
		reorient_cube_hSCSQS (cube1, &cube2);
		cube1 = cube2;
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlSCSQS (cube1, &cube2);
		cube1 = cube2;
	}
	if (inverse) {
		inverse_cubeSCSQS (cube1, &cube2);
		cube1 = cube2;
	}
	pack_cubeSCSQS (cube1, result_cube);
}

void
reorient_cubeSTAGE1_slow (const CubeStage1& init_cube, int sym, CubeStage1* result_cube)
{
#ifdef ELOC_INIT
	//sym is a symmetry code (0..15) for the 16 symmetries of the cube preserving the set of u- and d- layer edges
	int i;
	UINT u;
	CubeState cube1;
	CubeState cube2;
	Face t[24];
	UINT edge = init_cube.m_edge_ud_combo8;
	UINT co = init_cube.m_co;
	convert_stage1_to_std_cube (init_cube, &cube1);
	UINT sym1 = (sym/2) % 4;
	UINT sym2 = (sym/8) % 2;
	if (sym >= 16) {
		printf ("reorient_cubeSTAGE1: bad symmetry code\n");
		exit (1);
	}
	if (sym2 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
		reorient_cube_vCUBE (cube1, &cube2);
		reorient_cube_vCUBE (cube2, &cube1);
	}
	for (u = 0; u < sym1; ++u) {
		reorient_cube_hCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	convert_std_cube_to_stage1 (cube1, result_cube);
#endif
}

#ifdef STAGE1_INIT
void
reorient_cubeSTAGE1 (const CubeStage1& init_cube, int sym, CubeStage1* result_cube)
{
	//sym is a symmetry code (0..15) for the 16 symmetries of the cube preserving the set of u- and d- layer edges
	UINT edge = init_cube.m_edge_ud_combo8;
	UINT co = init_cube.m_co;
	result_cube->m_co = reorient_co[co][sym];
	result_cube->m_edge_ud_combo8 = reorient_s1edge[edge][sym];
}
#endif

void
reorient_cubeSTAGE2_slow (const CubeStage2& init_cube, int sym, CubeStage2* result_cube)
{
#ifdef STAGE2_INIT	//?
	//sym is a symmetry code (0..7) for the 8 symmetries of the cube using 180-degree rotations and reflection
	int i;
	UINT u;
	CubeState cube1;
	CubeState cube2;
	Face t[24];
	convert_stage2_to_std_cube (init_cube, &cube1);
	UINT sym1 = (sym/2) & 0x1;
	UINT sym2 = (sym/4) & 0x1;
	if (sym >= 8) {
		printf ("reorient_cubeSTAGE2: bad symmetry code\n");
		exit (1);
	}
	if (sym2 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
		reorient_cube_vCUBE (cube1, &cube2);
		reorient_cube_vCUBE (cube2, &cube1);
	}
	if (sym1 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	convert_std_cube_to_stage2 (cube1, result_cube);
#endif
}

#ifdef CLOC_INIT
UINT
reorient_centerFB_fast (UINT centerFB, int sym)
{
	UINT cloc_f = 0;
	UINT cloc_b = 0;
	get_clocFB (centerFB, &cloc_f, &cloc_b);
	UINT cloc1 = reorient_cenFB[cloc_f][sym];
	UINT cloc2 = reorient_cenFB[cloc_b][sym];
	if ((sym & 0x2) == 0) {
		return get_centerFB (cloc1, cloc2);
	}
	return get_centerFB (cloc2, cloc1);
}

UINT
reorient_centerFB_fast2 (UINT cloc_f, UINT cloc_b, int sym)
{
	UINT cloc1 = reorient_cenFB[cloc_f][sym];
	UINT cloc2 = reorient_cenFB[cloc_b][sym];
	if ((sym & 0x2) == 0) {
		return get_centerFB (cloc1, cloc2);
	}
	return get_centerFB (cloc2, cloc1);
}

void
get_clocFB (UINT centerFB, UINT* pcloc_f, UINT* pcloc_b)
{
	int i;
	UINT cenbm = eloc2ebm[centerFB / 70];
	UINT cenbm4of8 = bm4of8[centerFB % 70];
	Face t1[4], t2[4];
	int j1 = 0;
	int j2 = 0;
	for (i = 0; cenbm != 0; ++i) {
		if ((cenbm & 0x1) != 0) {
			if ((cenbm4of8 & 0x1) == 0) {
				t2[j2++] = i;
			} else {
				t1[j1++] = i;
			}
			cenbm4of8 >>= 1;
		}
		cenbm >>= 1;
	}
	int idx1 = 24*24*24*t1[0] + 24*24*t1[1] + 24*t1[2] + t1[3];
	int idx2 = 24*24*24*t2[0] + 24*24*t2[1] + 24*t2[2] + t2[3];
	*pcloc_f = c4_to_cloc[idx1];		//which is which here?
	*pcloc_b = c4_to_cloc[idx2];
}

UINT
get_centerFB (UINT cloc_f, UINT cloc_b)
{
	int i;
	UINT fbm = cloc_to_bm[cloc_f];
	UINT bbm = cloc_to_bm[cloc_b];
	UINT cenbm2 = fbm | bbm;
	int j1 = 0;
	UINT bm4of8b = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm2 & (1 << i)) != 0) {
			if ((fbm & (1 << i)) != 0) {
				bm4of8b |= (1 << j1);
			}
			++j1;
		}
	}
	UINT cen1 = bm4of8_to_70[bm4of8b];
	UINT cen2 = ebm2eloc[cenbm2];
	return 70*cen2 + cen1;
}
#endif

void
reorient_cubeSTAGE3_slow (const CubeStage3& init_cube, int sym, CubeStage3* result_cube)
{
#ifdef STAGE3_INIT
	//sym is a symmetry code (0..7) for the 8 symmetries of the cube using 180-degree rotations and reflection
	int i;
	UINT u;
	CubeState cube1;
	CubeState cube2;
	Face t[24];
	convert_stage3_to_std_cube (init_cube, &cube1);
	UINT sym1 = (sym/2) & 0x1;
	UINT sym2 = (sym/4) & 0x1;
	if (sym >= 8) {
		printf ("reorient_cubeSTAGE2: bad symmetry code\n");
		exit (1);
	}
	if (sym2 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
		reorient_cube_vCUBE (cube1, &cube2);
		reorient_cube_vCUBE (cube2, &cube1);
	}
	if (sym1 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	convert_std_cube_to_stage3 (cube1, result_cube);
#endif
}

void
reorient_cubeSTAGE3 (const CubeStage3& init_cube, int sym, CubeStage3* result_cube)
{
	//sym is a symmetry code (0..7) for the 8 symmetries of the cube
	//using 180-degree rotations and reflection.
#ifdef STAGE3_INIT
	USHORT edge = init_cube.m_edge;
	UINT cen = init_cube.m_centerLR;
	result_cube->m_edge = reorient_s3edge[edge][sym];
	result_cube->m_centerLR = reorient_s3cen[cen][sym];
#else
	reorient_cubeSTAGE3_slow (init_cube, sym, result_cube);
#endif
}

void
reorient_cubeSTAGE4_slow (const CubeStage4& init_cube, int sym, CubeStage4* result_cube)
{
	//sym is a symmetry code (0..15) for the 16 symmetries of the cube that keep
	//the top/bottom faces facing up and down.
	int i;
	UINT u;
	CubeState cube1;
	CubeState cube2;
	Face t[24];
	convert_stage4_to_std_cube (init_cube, &cube1);
	UINT sym0 = sym & 0x1;
	UINT sym1 = (sym >> 1) & 0x3;
	UINT sym2 = (sym >> 3) & 0x1;
	if (sym >= 16) {
		printf ("reorient_cubeSTAGE2: bad symmetry code\n");
		exit (1);
	}
	if (sym2 != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
		reorient_cube_vCUBE (cube1, &cube2);
		reorient_cube_vCUBE (cube2, &cube1);
	}
	if ((sym1 & 0x2) != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		reorient_cube_hCUBE (cube2, &cube1);
	}
	if ((sym1 & 0x1) != 0) {
		reorient_cube_hCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	if ((sym & 0x1) == 0x1) {
		mirror_cube_rlCUBE (cube1, &cube2);
		cube1 = cube2;
	}
	convert_std_cube_to_stage4 (cube1, result_cube);
}

void
reorient_cubeSTAGE4 (const CubeStage4& init_cube, int sym, CubeStage4* result_cube)
{
	//sym is a symmetry code (0..15) for the 16 symmetries of the cube
	//using 180-degree rotations and reflection.
#ifdef STAGE4_INIT
	UINT edge = init_cube.m_edge;
	USHORT cen = init_cube.m_centerUD;
	USHORT cor = init_cube.m_corner;
	result_cube->m_edge = reorient_s4edge[edge][sym];
	result_cube->m_centerUD = reorient_s4cen[cen][sym];
	result_cube->m_corner = reorient_s4cor[cor][sym];
#else
	reorient_cubeSTAGE4_slow (init_cube, sym, result_cube);
#endif
}

void
reorient_cube_hSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_hCORSQS[i];
		int sf = init_cube.m_cor[i];
		int df = reorient_hCORSQS[sf];
		result_cube->m_cor[pos] = reorient_hCORSQS[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_hEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hCENSQS[i];
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_hCENSQS[sf]/4;
	}
}

void
reorient_cube_vSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_vCORSQS[i];
		int sf = init_cube.m_cor[i];
		int df = reorient_vCORSQS[sf];
		result_cube->m_cor[pos] = reorient_vCORSQS[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_vEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vCENSQS[i];
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_vCENSQS[sf]/4;
	}
}

void
mirror_cube_rlSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		Face sf = init_cube.m_cor[i];
		result_cube->m_cor[i ^ 4] = sf ^ 4;  // note different than for regular corner numbering
	}
	for (i = 0; i < 24; ++i) {
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[mirror_rlEDGE[i]] = mirror_rlEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[mirror_rlCENSQS[i]] = mirror_rlCENSQS[sf]/4;
	}
}

void
inverse_cubeSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	Face t[24];
	Face x[6];
	for (i = 0; i < 8; ++i) {
		t[init_cube.m_cor[i]] = i;
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = t[i];
	}
	for (i = 0; i < 24; ++i) {
		t[init_cube.m_edge[i]] = i;
	}
	for (i = 0; i < 24; ++i) {
		result_cube->m_edge[i] = t[i];
	}
	for (i = 0; i < 6; ++i) {
		x[i] = 4*i;
	}
	for (i = 0; i < 24; ++i) {
		t[i] = x[init_cube.m_cen[i]]++; //Assign a "distinguishable" value for each cubie.
	}
	if (! (x[0] == 4 && x[1] == 8 && x[2] == 12 && x[3] == 16 && x[4] == 20 && x[5] == 24)) {
		printf ("inconsistency in centers\n");
		exit (1);
	}
	for (i = 0; i < 24; ++i) {
		result_cube->m_cen[t[i]] = i/4;
	}
}

void
reorient_cube_hSCSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_hCORSQS[i];
		int sf = init_cube.m_cor[i];
		int df = reorient_hCORSQS[sf];
		result_cube->m_cor[pos] = reorient_hCORSQS[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_hEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hSCCEN[i];
		Face sf = init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_hSCCEN[sf];
	}
}

void
reorient_cube_vSCSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_vCORSQS[i];
		int sf = init_cube.m_cor[i];
		int df = reorient_vCORSQS[sf];
		result_cube->m_cor[pos] = reorient_vCORSQS[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_vEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vSCCEN[i];
		Face sf = init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_vSCCEN[sf];
	}
}

void
mirror_cube_rlSCSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		Face sf = init_cube.m_cor[i];
		result_cube->m_cor[i ^ 4] = sf ^ 4;  // note different than for regular corner numbering
	}
	for (i = 0; i < 24; ++i) {
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[mirror_rlEDGE[i]] = mirror_rlEDGE[sf];

	}
	for (i = 0; i < 24; ++i) {
		Face sf = init_cube.m_cen[i];
		result_cube->m_cen[mirror_rlSCCEN[i]] = mirror_rlSCCEN[sf];
	}
}

void
inverse_cubeSCSQS (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	Face t[24];
	for (i = 0; i < 8; ++i) {
		t[init_cube.m_cor[i]] = i;
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = t[i];
	}
	for (i = 0; i < 24; ++i) {
		t[init_cube.m_edge[i]] = i;
	}
	for (i = 0; i < 24; ++i) {
		result_cube->m_edge[i] = t[i];
	}
	for (i = 0; i < 24; ++i) {
		t[init_cube.m_cen[i]] = i;
	}
	for (i = 0; i < 24; ++i) {
		result_cube->m_cen[i] = t[i];
	}
}

void
reorient_cube_hCUBE (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_hCOR[i];
		int sf = init_cube.m_cor[i];
		result_cube->m_cor[pos] = reorient_hCOR[sf & 0x7] | (sf & 0x18);
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_hEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_hCEN[i];
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_hCEN[sf]/4;
	}
}

void
reorient_cube_vCUBE (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		int pos = reorient_vCOR[i];
		int sf = init_cube.m_cor[i];
		int ori = ((sf & 0x18) >> 3) + reorientoc_vCOR[i];
		ori %= 3;
		result_cube->m_cor[pos] = reorient_vCOR[sf & 0x7] | (ori << 3);
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vEDGE[i];
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[pos] = reorient_vEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		int pos = reorient_vCEN[i];
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[pos] = reorient_vCEN[sf]/4;
	}
}

void
mirror_cube_rlCUBE (const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		Face sf = init_cube.m_cor[i];
		int ori = sf & 0x18;
		if (ori != 0) {
			ori = 0x18 - ori;
		}
		result_cube->m_cor[i ^ 1] = ((sf & 0x7) ^ 1) | ori;
	}
	for (i = 0; i < 24; ++i) {
		Face sf = init_cube.m_edge[i];
		result_cube->m_edge[mirror_rlEDGE[i]] = mirror_rlEDGE[sf];
	}
	for (i = 0; i < 24; ++i) {
		Face sf = 4*init_cube.m_cen[i];
		result_cube->m_cen[mirror_rlCEN[i]] = mirror_rlCEN[sf]/4;
	}
}

void
scrambleCUBE (CubeState* pcube, int move_count, const int* move_arr)
{
	//Now supports double-layer twist turns: Ufs (36) .. Bfs2 (53)
	int i;
	for (i = 0; i < move_count; ++i) {
		int mc = move_arr[i];
		if (mc >= Ufs) {
			pcube->do_move (dbltwists[mc - Ufs][0]);
			pcube->do_move (dbltwists[mc - Ufs][1]);
		} else {
			pcube->do_move (mc);
		}
	}
}

void
scrambleSQS (CubeSqsCoord* pcube, int move_count, const int* move_arr)
{
	int i;
	for (i = 0; i < move_count; ++i) {
		pcube->do_move (move_arr[i]/3);
	}
}

void
scrambleSCSQS (CubeSuperSqsCoord* pcube, int move_count, const int* move_arr)
{
	int i;
	for (i = 0; i < move_count; ++i) {
		pcube->do_move (move_arr[i]/3);
	}
}


UINT
perm_n_pack (UINT n, const Face* array_in)
{
	UINT idx;
	UINT i, j;

	idx = 0;

	for (i = 0; i < n; ++i) {
		idx *= (n - i);

		for (j = i + 1; j < n; ++j) {
			if (array_in[j] < array_in[i]) {
				++idx;
			}
		}
	}
	return idx;
}

void
perm_n_unpack (UINT n, UINT idx, Face* array_out)
{
	int i, j;
	int nn = static_cast<int>(n);

	for (i = nn - 1; i >= 0; --i) {
		array_out[i] = idx % (nn - i);
		idx /= (nn - i);

		for (j = i + 1; j < nn; ++j) {
			if (array_out[j] >= array_out[i]) {
				array_out[j]++;
			}
		}
	}
}

UINT
perm_n_pack2 (UINT n, UINT m, const Face* array_in)
{
	UINT idx;
	UINT i, j;
	UINT x;

	idx = 0;

	for (i = 0; i < m; ++i) {
		idx *= (n - i);

		x = 0;
		for (j = 0; j < i; ++j) {
			if (array_in[j] < array_in[i]) {
				++x;
			}
		}
		idx += array_in[i] - x;
	}
	return idx;
}

void
perm_n_unpack2 (UINT n, UINT m, UINT idx, Face* array_out)
{
	int i, j;
	int nn = static_cast<int>(n);
	int mm = static_cast<int>(m);
	int d = static_cast<int>(n - m);

	for (i = mm - 1; i >= 0; --i) {
		array_out[i] = idx % (nn - i);
		idx /= (nn - i);
		for (j = i + 1; j < mm; ++j) {
			if (array_out[j] >= array_out[i]) {
				array_out[j]++;
			}
		}
	}
}

bool
get_raw_coordinates (const CubeState& cube1, UINT* cp, UINT* co, UINT* ep, UINT* eo)
{
	int i;
	Face tmp[12];
	UINT oriente = 0;
	UINT orientc = 0;
	for (i = 0; i < 11; ++i) {	//don't want 12th edge orientation
		tmp[i] = cube1.m_edge[i] & 0xF;
		oriente = (oriente << 1) | cube1.m_edge[i] & 0x10;
	}
	tmp[11] = cube1.m_edge[11] & 0xF;
	*eo = oriente >> 4;
	*ep = perm_n_pack (12, &tmp[0]);
	for (i = 0; i < 7; ++i) {	//don't want 8th edge orientation
		tmp[i] = cube1.m_cor[i] & 0x7;
		orientc = 3*orientc + (cube1.m_cor[i] >> 3);
	}
	tmp[7] = cube1.m_cor[7] & 0x7;
	*cp = perm_n_pack (8, &tmp[0]);
	*co = orientc;
	return true;	//no validity checking done... presume valid cube...
}

bool
unpack_raw_coordinates (UINT cp, UINT co, UINT ep, UINT eo, CubeState* cube1)
{
	int i;
	Face tmp[12];
	UINT oriente = eo << 4;
	Face oriente_parity = 0;
	UINT orientc = co;
	UINT orientcmod3 = 0;
	perm_n_unpack (12, ep, &tmp[0]);
	for (i = 10; i >= 0; --i) {		//only 11 of twelve stored, the 12th is generated
		Face fo = oriente & 0x10;
		cube1->m_edge[i] = fo | tmp[i];
		oriente_parity ^= fo;
		oriente >>= 1;
	}
	cube1->m_edge[11] = oriente_parity | tmp[11];

	perm_n_unpack (8, cp, &tmp[0]);
	for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
		Face fo = orientc % 3;
		cube1->m_cor[i] = tmp[i] + (fo << 3);
		orientcmod3 += fo;
		orientc /= 3;
	}
	cube1->m_cor[7] = tmp[7] + (((24 - orientcmod3) % 3) << 3);
	return true;	//no validity checking done... presume valid cube coordinates...
}

bool
get_raw_coordinatesPERM (const CubeState& cube1, UINT* cp, UINT* ep)
{
	*ep = perm_n_pack (12, &(cube1.m_edge[0]));
	*cp = perm_n_pack (8, &(cube1.m_cor[0]));
	return true;	//no validity checking done... presume valid cube...
}

bool
unpack_raw_coordinatesPERM (UINT cp, UINT ep, CubeState* cube1)
{
	perm_n_unpack (12, ep, &(cube1->m_edge[0]));
	perm_n_unpack (8, cp, &(cube1->m_cor[0]));
	return true;	//no validity checking done... presume valid cube coordinates...
}

void
perm_n_init (int n, Face* out_arr)
{
	int i;

	for (i = 0; i < n; ++i) {
		out_arr[i] = i;
	}
}

void
three_cycle (Face* pArr, Face f1, Face f2, Face f3)
{
	Face temp;

	temp = pArr[f1];
	pArr[f1] = pArr[f2];
	pArr[f2] = pArr[f3];
	pArr[f3] = temp;
}

void
four_cycle (Face* pArr, Face f1, Face f2, Face f3, Face f4)
{
	Face temp;

	temp = pArr[f1];
	pArr[f1] = pArr[f2];
	pArr[f2] = pArr[f3];
	pArr[f3] = pArr[f4];
	pArr[f4] = temp;
}

void
perm_n_compose (int n, const Face* perm0_in, const Face* perm1_in, Face* perm_out)
{
	int i;

	for (i = 0; i < n; ++i) {
		perm_out[i] = perm0_in[perm1_in[i]];
	}
}

#ifdef PRUNING_TABLES
void
initGEN (UBYTE* ptable, int npositions)
{
	UINT i;
	UINT n = npositions/2 + (npositions & 0x1);
	prune_table_count = 0;
	for (i = 0; i < n; ++i) {
		ptable[i] = 0xFF;
	}
}

void
analyzeGEN (UBYTE* ptable, UINT npositions, int nmoves, int* pmove_map, int solved_positions, int* psolved_list, int metric, int stage)
{
	int i;
	UINT idx;
	int dist;
	int count = 0;

	int max_dist = 14;	//MAX_DISTANCE;
	initGEN (ptable, npositions);
	for (i = 0; i < solved_positions; ++i) {
		add_to_cube_listGEN (ptable, psolved_list[i], 0, metric);
	}
	UINT new_count = prune_table_count;	//?????
	printf ("dist %2d: pos %8d total %8d\n", 0, new_count, prune_table_count);
	for (dist = 1; dist <= max_dist && new_count > 0; ++dist) {
		UINT old_count = prune_table_count;
		for (idx = 0; idx < npositions; ++idx) {
			UINT idx2 = idx/2;
			UINT j = idx & 0x1;
			if (get_dist_4bit (idx, ptable) == dist - 1) {
				generateGEN (idx, dist, metric, ptable, nmoves, pmove_map, stage);
			}
		}
		new_count = prune_table_count - old_count;
		printf ("dist %2d: pos %8d total %8u\n", dist, new_count, prune_table_count);
	}
}

void
generateGEN (UINT idx, int dist, int metric, UBYTE* ptable, int nmoves, int* pmove_map, int stage)
{
	int i, j;

	for (i = 0; i < nmoves; ++i) {
		UINT idx2 = do_moveGEN (idx, metric, pmove_map[i], stage);
		add_to_cube_listGEN (ptable, idx2, dist, metric);
	}
}

bool
check_newGEN (UINT idx, UBYTE* ptable)
{
	return get_dist_4bit (idx, ptable) == 0xF;	//only "unknown" positions are considered "new"
}

void
add_to_cube_listGEN (UBYTE* ptable, UINT idx, int dist, int metric)
{
	if (check_newGEN (idx, ptable)) {
		set_dist_4bit (idx, dist, ptable);
		++prune_table_count;
	}
}

UINT
do_moveGEN (UINT idx, int metric, int move_code, int stage)
{
	UINT idx2 = idx;
	switch (stage) {
	case 5:
		{
			CubeSqsCoord cube1;
			CubeSqsCoord cube2;
			cube1.init ();
			cube1.m_cen12x12x12 = idx;	//need to know that this is the "centers" analysis
			cube1.do_move (move_code);	//need loop to do more complicated moves than single-slice moves
			idx2 = cube1.m_cen12x12x12;
		}
		break;
	}
	return idx2;
}
//===========
CubePruningTable::CubePruningTable (UINT num_positions, UBYTE* ptable, void* move_func, int stage, int metric, UINT cencoredg) :
	m_num_positions (num_positions),
	m_ptable (ptable),
	m_do_move_func (move_func),
	m_num_moves (0),
	m_move_list (NULL),
	m_num_moves2 (0),
	m_move_list2 (NULL),
	m_num_solved (0),
	m_psolved (NULL),
	m_stage (stage),
	m_metric (metric),
	m_cencoredg (cencoredg),
	m_count (0)
{
}

CubePruningTable::~CubePruningTable ()
{
	if (m_move_list != NULL) {
		delete [] m_move_list;
	}
	if (m_move_list2 != NULL) {
		delete [] m_move_list2;
	}
}

void
CubePruningTable::init_move_list (int dim2, int num_moves, int* move_list)
{
	int i, j;
	m_num_moves = num_moves;
	m_move_list = new int[3*m_num_moves];
	switch (dim2) {
	case 0:
		for (i = 0; i < m_num_moves; ++i) {
			m_move_list[3*i] = i;
			m_move_list[3*i+1] = -1;
			m_move_list[3*i+2] = -1;
		}
		break;
	case 1:
		for (i = 0; i < m_num_moves; ++i) {
			m_move_list[3*i] = move_list[i];
			m_move_list[3*i+1] = -1;
			m_move_list[3*i+2] = -1;
		}
		break;
	case 2:
		{
			int* p = move_list;
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = *p++;
				m_move_list[3*i+1] = *p++;
				m_move_list[3*i+2] = -1;
			}
		}
		break;
	case 3:
		{
			int* p = move_list;
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = *p++;
				m_move_list[3*i+1] = *p++;
				m_move_list[3*i+2] = *p++;
			}
		}
		break;
	default:
		printf ("CubePruningTable::init_move_list call ignored\n");
	}
}

void
CubePruningTable::init_move_list2 (int dim2, int num_moves, int* move_list)
{
	int i, j;
	m_num_moves2 = num_moves;
	m_move_list2 = new int[3*m_num_moves];
	switch (dim2) {
	case 0:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = i;
			m_move_list2[3*i+1] = -1;
			m_move_list2[3*i+2] = -1;
		}
		break;
	case 1:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = move_list[i];
			m_move_list2[3*i+1] = -1;
			m_move_list2[3*i+2] = -1;
		}
		break;
	case 2:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = move_list[2*i];
			m_move_list2[3*i+1] = move_list[2*i + 1];
			m_move_list2[3*i+2] = -1;
		}
		break;
	default:
		printf ("CubePruningTable::init_move_list2 call ignored\n");
	}
}

void
CubePruningTable::init_solved_list (int count, int* solved_list)
{
	m_num_solved = count;
	m_psolved = solved_list;	//Assume points to statically allocated array
}

void
CubePruningTable::init ()
{
	UINT i;
	UINT n = m_num_positions/2 + (m_num_positions & 0x1);
	m_count = 0;
	for (i = 0; i < n; ++i) {
		m_ptable[i] = 0xFF;
	}
}

void
CubePruningTable::analyze ()
{
	int i;
	UINT idx;
	int dist;
	int max_dist = 14;	//MAX_DISTANCE;
	init ();
	for (i = 0; i < m_num_solved; ++i) {
		add_to_table (m_psolved[i], 0);
	}
	UINT new_count = m_count;
#ifdef OUTPUT_DIAGNOSTICS
	printf ("dist %2d: pos %8d total %8d\n", 0, new_count, m_count);
#endif
	for (dist = 1; dist <= max_dist && new_count > 0; ++dist) {
		UINT old_count = m_count;
		for (idx = 0; idx < m_num_positions; ++idx) {
#ifdef USE_TITLE_BAR
			if (idx % 200000 == 0) {
				char str[64];
				sprintf (&str[0], "title analyzePRUNE %u", idx);
				system (&str[0]);
			}
#endif
			//UINT idx2 = idx/2;
			//UINT j = idx & 0x1;
			int dx = get_dist_4bit (idx, m_ptable);
			if (m_num_moves2 > 0 && dist >= 2 && dx == dist - 2) {
				generate2 (idx, dist);
			}
			if (dx == dist - 1) {
				generate1 (idx, dist);
			}
		}
		new_count = m_count - old_count;
#ifdef OUTPUT_DIAGNOSTICS
		printf ("dist %2d: pos %8d total %8u\n", dist, new_count, m_count);
#endif
		//special case: distance 1 could have 0 positions when there are "moves" that count as 2 moves.
		if (new_count == 0 && m_num_moves2 > 0 && dist == 1) {
			new_count = 1;	//fake new count to prevent exiting loop prematurely.
		}
	}
}

void
CubePruningTable::generate1 (UINT idx, int dist)
{
	int i, j;

	for (i = 0; i < m_num_moves; ++i) {
		UINT idx2 = callfunc (m_do_move_func, idx, m_move_list[3*i]);
		for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
			idx2 = callfunc (m_do_move_func, idx2, m_move_list[3*i + j]);
		}
		add_to_table (idx2, dist);
	}
}

void
CubePruningTable::generate2 (UINT idx, int dist)
{
	int i, j;

	for (i = 0; i < m_num_moves2; ++i) {
		UINT idx2 = callfunc (m_do_move_func, idx, m_move_list2[3*i]);
		for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
			idx2 = callfunc (m_do_move_func, idx2, m_move_list2[3*i + j]);
		}
		add_to_table (idx2, dist);
	}
}

bool
CubePruningTable::check_new (UINT idx) const
{
	return get_dist_4bit (idx, m_ptable) == 0xF;	//only "unknown" positions are considered "new"
}

void
CubePruningTable::add_to_table (UINT idx, int dist)
{
	if (get_dist_4bit (idx, m_ptable) == 0xF) {
		set_dist_4bit (idx, dist, m_ptable);
		++m_count;
	}
}

void
CubePruningTableMgr::init_pruning_tables (int metric)
{
	int i;
	FILE* prunef = NULL;
	static int solved_table[24];
	static int tmp_list[64*3];
	char fname[320];
	CubeStage1 stage1_solved, stage1_solved2;
	CubeStage2 stage2_solved, stage2_solved2;
	CubeStage3 stage3_solved, stage3_solved2;
	CubeStage4 stage4_solved, stage4_solved2;
	static Face switch_list[5][4] = {
		{ 17, 19, 20, 22 },
		{ 17, 19, 21, 23 },
		{ 17, 18, 21, 22 },
		{ 17, 18, 20, 23 },
		{ 18, 19, 22, 23 }
	};
	CubeState cs1;
	if (! (metric == 0 || metric == 1 || metric == 2)) {
		printf ("Metric %d not supported.\n", metric);
		exit (1);
	}
	printf ("Creating pruning tables for %s turns.\nStage1...\n", metric_long_names[metric]);
	stage1_solved.init ();
	solved_table[0] = stage1_solved.m_co;
	stage1_solved2 = stage1_solved;
	stage1_solved2.do_whole_cube_move (2);
	stage1_solved2.do_whole_cube_move (1);
	solved_table[1] = stage1_solved2.m_co;
	stage1_solved2.do_whole_cube_move (2);
	stage1_solved2.do_whole_cube_move (1);
	solved_table[2] = stage1_solved2.m_co;
	switch (metric) {
	case 0:		// single-slice
		pcpt_cor1 = new CubePruningTable (N_CORNER_ORIENT, &prune_table_cor1[0], (void*) do_move_COR_STAGE1_STM, 1, metric, 0x2);
		pcpt_cor1->init_move_list (0, N_BASIC_MOVES, NULL);
		break;
	case 1:		// twist
		pcpt_cor1 = new CubePruningTable (N_CORNER_ORIENT, &prune_table_cor1[0], (void*) do_move_COR_STAGE1_STM, 1, metric, 0x2);
		for (i = 0; i < N_STAGE1_TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage1_twist_moves[i][0];
			tmp_list[2*i+1] = stage1_twist_moves[i][1];
		}
		pcpt_cor1->init_move_list (2, N_STAGE1_TWIST_MOVES, &tmp_list[0]);
		break;
	case 2:		// block
		pcpt_cor1 = new CubePruningTable (N_CORNER_ORIENT, &prune_table_cor1[0], (void*) do_move_COR_STAGE1_STM, 1, metric, 0x2);
		for (i = 0; i < N_STAGE1_BLOCK_MOVES; ++i) {
			tmp_list[2*i] = stage1_block_moves[i][0];
			tmp_list[2*i+1] = stage1_block_moves[i][1];
		}
		pcpt_cor1->init_move_list (2, N_STAGE1_BLOCK_MOVES, &tmp_list[0]);
		break;
	}
	pcpt_cor1->init_solved_list (3, &solved_table[0]);
	pcpt_cor1->analyze ();

	sprintf (&fname[0], "%sstage1_%s_edg_prune.rbk", &datafiles_path[0], metric_names[metric]);
	prunef = fopen (&fname[0], "rb");
	if (prunef == NULL) {
		solved_table[0] = stage1_solved.m_edge_ud_combo8;
		stage1_solved2 = stage1_solved;
		stage1_solved2.do_whole_cube_move (2);
		stage1_solved2.do_whole_cube_move (1);
		solved_table[1] = stage1_solved2.m_edge_ud_combo8;
		stage1_solved2.do_whole_cube_move (2);
		stage1_solved2.do_whole_cube_move (1);
		solved_table[2] = stage1_solved2.m_edge_ud_combo8;
		switch (metric) {
		case 0:
			pcpt_edg1 = new CubePruningTable (N_EDGE_COMBO8, &prune_table_edg1[0],
				(void*) do_move_EDGE_STAGE1_STM, 1, metric, 0x1);
			pcpt_edg1->init_move_list (0, N_BASIC_MOVES, NULL);
			break;
		case 1:
			pcpt_edg1 = new CubePruningTable (N_EDGE_COMBO8, &prune_table_edg1[0],
				(void*) do_move_EDGE_STAGE1_STM, 1, metric, 0x1);
				pcpt_edg1->init_move_list (2, N_STAGE1_TWIST_MOVES, &tmp_list[0]);
			break;
		case 2:
			pcpt_edg1 = new CubePruningTable (N_EDGE_COMBO8, &prune_table_edg1[0],
				(void*) do_move_EDGE_STAGE1_STM, 1, metric, 0x1);
				pcpt_edg1->init_move_list (2, N_STAGE1_BLOCK_MOVES, &tmp_list[0]);
			break;
		}
		pcpt_edg1->init_solved_list (3, &solved_table[0]);
		pcpt_edg1->analyze ();
		prunef = fopen (&fname[0], "wb");
		if (prunef != NULL) {
			printf ("Creating pruning table file '%s'.\n", &fname[0]);
			int n = fwrite (&prune_table_edg1[0], 1, (N_EDGE_COMBO8 + 1)/2, prunef);
			if (n != (N_EDGE_COMBO8 + 1)/2) {
				printf ("Error writing pruning table file '%s'.\n", &fname[0]);
			}
			fclose (prunef);
		} else {
			printf ("Warning: Failed to create pruning file %s\n", &fname[0]);
		}
	} else {
		int n = fread (&prune_table_edg1[0], 1, (N_EDGE_COMBO8 + 1)/2, prunef);
		if (n != (N_EDGE_COMBO8 + 1)/2) {
			printf ("Error reading pruning table file '%s'.\n", &fname[0]);
		}
		fclose (prunef);
	}
	printf ("Stage2...\n");
	UINT clocfx, clocbx;
	sprintf (&fname[0], "%sstage2_%s_edgcen_prune.rbk", &datafiles_path[0], metric_names[metric]);
	prunef = fopen (&fname[0], "rb");
	if (prunef == NULL) {
		int n_moves;
		stage2_solved.init ();
		stage2_cen_to_cloc4s (stage2_solved.m_centerFB, &clocfx, &clocbx);
		solved_table[0] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved.m_edge;
		stage2_solved2 = stage2_solved;
		stage2_solved2.do_whole_cube_move (1);
		stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
		solved_table[1] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
		cs1.init ();
		cs1.invert_fbcen ();
		convert_std_cube_to_stage2 (cs1, &stage2_solved2);
		stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
		solved_table[2] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
		stage2_solved2.do_whole_cube_move (1);
		stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
		solved_table[3] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
		for (i = 0; i < 5; ++i) {
			int j;
			cs1.init ();
			for (j = 0; j < 4; ++j) {
				cs1.m_cen[switch_list[i][j]] ^= 1;
			}
			convert_std_cube_to_stage2 (cs1, &stage2_solved2);
			stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
			solved_table[4*i + 4] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
			stage2_solved2.do_whole_cube_move (1);
			stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
			solved_table[4*i + 5] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
			cs1.invert_fbcen ();
			convert_std_cube_to_stage2 (cs1, &stage2_solved2);
			stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
			solved_table[4*i + 6] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
			stage2_solved2.do_whole_cube_move (1);
			stage2_cen_to_cloc4s (stage2_solved2.m_centerFB, &clocfx, &clocbx);
			solved_table[4*i + 7] = N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
		}
		switch (metric) {
		case 0:
			pcpt_edgcen2 = new CubePruningTable (N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS, &prune_table_edgcen2[0], (void*) do_move_EDGCENF_STAGE2_STM, 2, metric, 0x5);
			if (use_old_stage2) {
				n_moves = N_STAGE2_SLICE_MOVES_OLD;
			} else {
				n_moves = N_STAGE2_SLICE_MOVES;
			}
			pcpt_edgcen2->init_move_list (0, n_moves, NULL);
			break;
		case 1:
			pcpt_edgcen2 = new CubePruningTable (N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS, &prune_table_edgcen2[0], (void*) do_move_EDGCENF_STAGE2_STM, 2, metric, 0x5);
			for (i = 0; i < N_STAGE2_TWIST_MOVES; ++i) {
				tmp_list[2*i] = stage2_twist_moves[i][0];
				tmp_list[2*i+1] = stage2_twist_moves[i][1];
			}
			pcpt_edgcen2->init_move_list (2, N_STAGE2_TWIST_MOVES, &tmp_list[0]);
			for (i = 0; i < N_STAGE2_2TWIST_MOVES; ++i) {
				tmp_list[2*i] = stage2_2twist_moves[i][0];
				tmp_list[2*i+1] = stage2_2twist_moves[i][1];
			}
			pcpt_edgcen2->init_move_list2 (2, N_STAGE2_2TWIST_MOVES, &tmp_list[0]);
			break;
		case 2:
			pcpt_edgcen2 = new CubePruningTable (N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS, &prune_table_edgcen2[0], (void*) do_move_EDGCENF_STAGE2_STM, 2, metric, 0x5);
			for (i = 0; i < N_STAGE2_BLOCK_MOVES; ++i) {
				tmp_list[2*i] = stage2_block_moves[i][0];
				tmp_list[2*i+1] = stage2_block_moves[i][1];
			}
			pcpt_edgcen2->init_move_list (2, N_STAGE2_BLOCK_MOVES, &tmp_list[0]);
		}
		pcpt_edgcen2->init_solved_list (24, &solved_table[0]);
		pcpt_edgcen2->analyze ();
		prunef = fopen (&fname[0], "wb");
		if (prunef != NULL) {
			printf ("Creating pruning table file '%s'.\n", &fname[0]);
			int n = fwrite (&prune_table_edgcen2[0], 1, N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2, prunef);
			if (n != N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2) {
				printf ("Error writing pruning table file '%s'.\n", &fname[0]);
			}
			fclose (prunef);
		} else {
			printf ("Warning: Failed to create pruning file %s\n", &fname[0]);
		}
	} else {
		int n = fread (&prune_table_edgcen2[0], 1, N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2, prunef);
		if (n != N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2) {
			printf ("Error reading pruning table file '%s'.\n", &fname[0]);
		}
		fclose (prunef);
	}

	//Stage 3
	printf ("Stage3...\n");
	for (i = 0; i < STAGE3_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		solved_table[i] = stage3_solved_centers[i];
	}
	switch (metric) {
	case 0:
		pcpt_cen3 = new CubePruningTable (N_STAGE3_CENTER_CONFIGS,
			&prune_table_cen3[0], (void*) do_move_CEN_STAGE3_STM, 3, metric, 0x4);
		pcpt_cen3->init_move_list (0, N_STAGE3_SLICE_MOVES, NULL);
		break;
	case 1:
		pcpt_cen3 = new CubePruningTable (N_STAGE3_CENTER_CONFIGS,
			&prune_table_cen3[0], (void*) do_move_CEN_STAGE3_STM, 3, metric, 0x4);
		for (i = 0; i < N_STAGE3_TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage3_twist_moves[i][0];
			tmp_list[2*i+1] = stage3_twist_moves[i][1];
		}
		pcpt_cen3->init_move_list (2, N_STAGE3_TWIST_MOVES, &tmp_list[0]);
		for (i = 0; i < N_STAGE3_2TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage3_2twist_moves[i][0];
			tmp_list[2*i+1] = stage3_2twist_moves[i][1];
		}
		pcpt_cen3->init_move_list2 (2, N_STAGE3_2TWIST_MOVES, &tmp_list[0]);
		break;
	case 2:
		pcpt_cen3 = new CubePruningTable (N_STAGE3_CENTER_CONFIGS,
			&prune_table_cen3[0], (void*) do_move_CEN_STAGE3_STM, 3, metric, 0x4);
		for (i = 0; i < N_STAGE3_BLOCK_MOVES; ++i) {
			tmp_list[2*i] = stage3_block_moves[i][0];
			tmp_list[2*i+1] = stage3_block_moves[i][1];
		}
		pcpt_cen3->init_move_list (2, N_STAGE3_BLOCK_MOVES, &tmp_list[0]);
		break;
	}
	pcpt_cen3->init_solved_list (STAGE3_NUM_SOLVED_CENTER_CONFIGS, &solved_table[0]);
	pcpt_cen3->analyze ();

	stage3_solved.init ();
	solved_table[0] = stage3_solved.m_edge;
	switch (metric) {
	case 0:
		pcpt_edg3 = new CubePruningTable (N_STAGE3_EDGE_PAR*N_STAGE3_EDGE_CONFIGS, &prune_table_edg3[0], (void*) do_move_EDGE_STAGE3_STM, 3, metric, 0x1);
		break;
	case 1:
		pcpt_edg3 = new CubePruningTable (N_STAGE3_EDGE_PAR*N_STAGE3_EDGE_CONFIGS, &prune_table_edg3[0], (void*) do_move_EDGE_STAGE3_STM, 3, metric, 0x1);
		for (i = 0; i < N_STAGE3_TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage3_twist_moves[i][0];
			tmp_list[2*i+1] = stage3_twist_moves[i][1];
		}
		pcpt_edg3->init_move_list (2, N_STAGE3_TWIST_MOVES, &tmp_list[0]);
		for (i = 0; i < N_STAGE3_2TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage3_2twist_moves[i][0];
			tmp_list[2*i+1] = stage3_2twist_moves[i][1];
		}
		pcpt_edg3->init_move_list2 (2, N_STAGE3_2TWIST_MOVES, &tmp_list[0]);
		break;
	case 2:
		pcpt_edg3 = new CubePruningTable (N_STAGE3_EDGE_PAR*N_STAGE3_EDGE_CONFIGS, &prune_table_edg3[0], (void*) do_move_EDGE_STAGE3_STM, 3, metric, 0x1);
		pcpt_edg3->init_move_list (2, N_STAGE3_BLOCK_MOVES, &tmp_list[0]);
		break;
	}
	pcpt_edg3->init_move_list (0, N_STAGE3_SLICE_MOVES, NULL);
	pcpt_edg3->init_solved_list (1, &solved_table[0]);
	pcpt_edg3->analyze ();

	//Stage 4
	printf ("Stage4...\n");
	stage4_solved.init ();
	for (i = 0; i < STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		solved_table[i] = N_STAGE4_CENTER_CONFIGS*stage4_solved.m_corner + bm4of8_to_70[stage4_solved_centers_bm[i]];
	}
	switch (metric) {
	case 0:
		pcpt_cencor4 = new CubePruningTable (N_STAGE4_CORNER_CONFIGS*N_STAGE4_CENTER_CONFIGS,
			&prune_table_cencor4[0], (void*) do_move_CENCOR_STAGE4_STM, 4, metric, 0x6);
		pcpt_cencor4->init_move_list (0, N_STAGE4_SLICE_MOVES, NULL);
		break;
	case 1:
		pcpt_cencor4 = new CubePruningTable (N_STAGE4_CORNER_CONFIGS*N_STAGE4_CENTER_CONFIGS,
			&prune_table_cencor4[0], (void*) do_move_CENCOR_STAGE4_STM, 4, metric, 0x6);
		for (i = 0; i < N_STAGE4_TWIST_MOVES; ++i) {
			tmp_list[2*i] = stage4_twist_moves[i][0];
			tmp_list[2*i+1] = stage4_twist_moves[i][1];
		}
		pcpt_cencor4->init_move_list (2, N_STAGE4_TWIST_MOVES, &tmp_list[0]);
		break;
	case 2:
		pcpt_cencor4 = new CubePruningTable (N_STAGE4_CORNER_CONFIGS*N_STAGE4_CENTER_CONFIGS,
			&prune_table_cencor4[0], (void*) do_move_CENCOR_STAGE4_STM, 4, metric, 0x6);
		for (i = 0; i < N_STAGE4_BLOCK_MOVES; ++i) {
			tmp_list[2*i] = stage4_block_moves[i][0];
			tmp_list[2*i+1] = stage4_block_moves[i][1];
		}
		pcpt_cencor4->init_move_list (2, N_STAGE4_BLOCK_MOVES, &tmp_list[0]);
		break;
	}
	pcpt_cencor4->init_solved_list (STAGE4_NUM_SOLVED_CENTER_CONFIGS, &solved_table[0]);
	pcpt_cencor4->analyze ();

	sprintf (&fname[0], "%sstage4_%s_edgcen_prune.rbk", &datafiles_path[0], metric_names[metric]);
	prunef = fopen (&fname[0], "rb");
	if (prunef == NULL) {
		for (i = 0; i < STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i) {
			solved_table[i] = N_STAGE4_CENTER_CONFIGS*stage4_solved.m_edge + bm4of8_to_70[stage4_solved_centers_bm[i]];
		}
		switch (metric) {
		case 0:
			pcpt_edgcen4 = new CubePruningTable (N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS,
				&prune_table_edgcen4[0], (void*) do_move_EDGCEN_STAGE4_STM, 4, metric, 0x5);
			pcpt_edgcen4->init_move_list (0, N_STAGE4_SLICE_MOVES, NULL);
			break;
		case 1:
			pcpt_edgcen4 = new CubePruningTable (N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS,
				&prune_table_edgcen4[0], (void*) do_move_EDGCEN_STAGE4_STM, 4, metric, 0x5);
			pcpt_edgcen4->init_move_list (2, N_STAGE4_TWIST_MOVES, &tmp_list[0]);
			break;
		case 2:
			pcpt_edgcen4 = new CubePruningTable (N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS,
				&prune_table_edgcen4[0], (void*) do_move_EDGCEN_STAGE4_STM, 4, metric, 0x5);
			pcpt_edgcen4->init_move_list (2, N_STAGE4_BLOCK_MOVES, &tmp_list[0]);
			break;
		}
		pcpt_edgcen4->init_solved_list (STAGE4_NUM_SOLVED_CENTER_CONFIGS, &solved_table[0]);
		pcpt_edgcen4->analyze ();
		prunef = fopen (&fname[0], "wb");
		if (prunef != NULL) {
			printf ("Creating pruning table file '%s'.\n", &fname[0]);
			int n = fwrite (&prune_table_edgcen4[0], 1, N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2, prunef);
			if (n != N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2) {
				printf ("Error writing pruning table file '%s'.\n", &fname[0]);
			}
			fclose (prunef);
		} else {
			printf ("Warning: Failed to create pruning file %s\n", &fname[0]);
		}
	} else {
		int n = fread (&prune_table_edgcen4[0], 1, N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2, prunef);
		if (n != N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2) {
			printf ("Error reading pruning table file '%s'.\n", &fname[0]);
		}
		fclose (prunef);
	}
	//Stage 5
	printf ("Stage5...\n");
	CubeSqsCoord sqs_solved, sqs_solved2;
	sqs_solved.init ();
	solved_table[0] = N_SQS_CORNER_PERM*sqs_solved.m_cen12x12x12 + sqs_solved.m_cp96;
	for (i = 1; i < 4; ++i) {
		sqs_solved2 = sqs_solved;
		sqs_solved2.do_whole_cube_move (i);
		solved_table[i] = N_SQS_CORNER_PERM*sqs_solved2.m_cen12x12x12 + sqs_solved2.m_cp96;
	}
	switch (metric) {
	case 0:
		pcpt_cencor5 = new CubePruningTable (N_SQS_CENTER_PERM*N_SQS_CORNER_PERM, &prune_table_cencor5[0], (void*) do_move_CENCOR_STAGE5, 5, 0, 0x6);
		pcpt_cencor5->init_move_list (0, 12, NULL);
		break;
	case 1:
		pcpt_cencor5 = new CubePruningTable (N_SQS_CENTER_PERM*N_SQS_CORNER_PERM, &prune_table_cencor5[0], (void*) do_move_CENCOR_STAGE5, 5, 0, 0x6);
		for (i = 0; i < N_SQ_TWIST_MOVES; ++i) {
			tmp_list[2*i] = sq_twist_moves[i][0];
			tmp_list[2*i+1] = sq_twist_moves[i][1];
		}
		pcpt_cencor5->init_move_list (2, N_SQ_TWIST_MOVES, &tmp_list[0]);
		break;
	case 2:
		pcpt_cencor5 = new CubePruningTable (N_SQS_CENTER_PERM*N_SQS_CORNER_PERM, &prune_table_cencor5[0], (void*) do_move_CENCOR_STAGE5, 5, 0, 0x6);
		for (i = 0; i < N_SQ_BLOCK_MOVES; ++i) {
			tmp_list[2*i] = sq_block_moves[i][0];
			tmp_list[2*i+1] = sq_block_moves[i][1];
		}
		pcpt_cencor5->init_move_list (2, N_SQ_BLOCK_MOVES, &tmp_list[0]);
		break;
	}
	pcpt_cencor5->init_solved_list (4, &solved_table[0]);
	pcpt_cencor5->analyze ();

	sprintf (&fname[0], "%sstage5_%s_edgcor_prune.rbk", &datafiles_path[0], metric_names[metric]);
	prunef = fopen (&fname[0], "rb");
	if (prunef == NULL) {
		sqs_solved.init ();
		solved_table[0] = N_SQS_CORNER_PERM*sqs_solved.m_ep96x96x96 + sqs_solved.m_cp96;
		for (i = 1; i < 4; ++i) {
			sqs_solved2 = sqs_solved;
			sqs_solved2.do_whole_cube_move (i);
			solved_table[i] = N_SQS_CORNER_PERM*sqs_solved2.m_ep96x96x96 + sqs_solved2.m_cp96;
		}
		switch (metric) {
		case 0:
			pcpt_edgcor5 = new CubePruningTable (N_SQS_EDGE_PERM*N_SQS_CORNER_PERM, &prune_table_edgcor5[0], (void*) do_move_EDGCOR_STAGE5, 5, 0, 0x3);
			pcpt_edgcor5->init_move_list (0, 12, NULL);
			break;
		case 1:
			pcpt_edgcor5 = new CubePruningTable (N_SQS_EDGE_PERM*N_SQS_CORNER_PERM, &prune_table_edgcor5[0], (void*) do_move_EDGCOR_STAGE5, 5, 0, 0x3);
			pcpt_edgcor5->init_move_list (2, N_SQ_TWIST_MOVES, &tmp_list[0]);
			break;
		case 2:
			pcpt_edgcor5 = new CubePruningTable (N_SQS_EDGE_PERM*N_SQS_CORNER_PERM, &prune_table_edgcor5[0], (void*) do_move_EDGCOR_STAGE5, 5, 0, 0x3);
			pcpt_edgcor5->init_move_list (2, N_SQ_BLOCK_MOVES, &tmp_list[0]);
			break;
		}
		pcpt_edgcor5->init_solved_list (4, &solved_table[0]);
		pcpt_edgcor5->analyze ();
		prunef = fopen (&fname[0], "wb");
		if (prunef != NULL) {
			printf ("Creating pruning table file '%s'.\n", &fname[0]);
			int n = fwrite (&prune_table_edgcor5[0], 1, N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2, prunef);
			if (n != N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2) {
				printf ("Error writing pruning table file '%s'.\n", &fname[0]);
			}
			fclose (prunef);
		} else {
			printf ("Warning: Failed to create pruning file %s\n", &fname[0]);
		}
	} else {
		int n = fread (&prune_table_edgcor5[0], 1, N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2, prunef);
		if (n != N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2) {
			printf ("Error reading pruning table file '%s'.\n", &fname[0]);
		}
		fclose (prunef);
	}
}

UINT
do_move_CEN_STAGE5 (UINT idx, int move_code)
{
	CubeSqsCoord cube1;
	cube1.init ();
	cube1.m_cen12x12x12 = idx;	//need to know that this is the "centers" analysis
	cube1.do_move (move_code);	//need loop to do more complicated moves than single-slice moves
	return cube1.m_cen12x12x12;
}

UINT
do_move_EDGE_STAGE5 (UINT idx, int sqs_move_code)
{
	UINT ep0 = idx%96;
	UINT ep1 = (idx/96) % 96;
	UINT ep2 = idx/(96*96);
	return squares_move_edges (ep0, sqs_move_code, 0) +
		96*squares_move_edges (ep1, sqs_move_code, 1) +
		96*96*squares_move_edges (ep2, sqs_move_code, 2);
}

UINT
do_move_COR_STAGE5 (UINT idx, int move_code)
{
	CubeSqsCoord cube1;
	cube1.init ();
	cube1.m_cp96 = idx;	//need to know that this is the "corners" analysis
	cube1.do_move (move_code);	//need loop to do more complicated moves than single-slice moves
	return cube1.m_cp96;
}

UINT
do_move_CENCOR_STAGE5 (UINT idx, int sqs_move_code)
{
	UINT cen = idx/N_SQS_CORNER_PERM;
	UINT cp = idx % N_SQS_CORNER_PERM;
	UINT cp96 = squares_move_corners (cp, sqs_move_code);
	UINT cen0 = cen % 12;
	UINT cen1 = (cen/12) % 12;
	UINT cen2 = cen/(12*12); 
	UINT cen12x12x12 = squares_move_centers (cen0, sqs_move_code, 0) +
		12*squares_move_centers (cen1, sqs_move_code, 1) +
		12*12*squares_move_centers (cen2, sqs_move_code, 2);
	return N_SQS_CORNER_PERM*cen12x12x12 + cp96;
}

UINT
do_move_EDGCOR_STAGE5 (UINT idx, int sqs_move_code)
{
	UINT ep96x96x96 = idx/N_SQS_CORNER_PERM;
	UINT cp = idx % N_SQS_CORNER_PERM;
	UINT cp96 = squares_move_corners (cp, sqs_move_code);
	UINT ep0 = ep96x96x96%96;
	UINT ep1 = (ep96x96x96/96) % 96;
	UINT ep2 = ep96x96x96/(96*96);
	ep96x96x96 = squares_move_edges (ep0, sqs_move_code, 0) +
		96*squares_move_edges (ep1, sqs_move_code, 1) +
		96*96*squares_move_edges (ep2, sqs_move_code, 2);
	return N_SQS_CORNER_PERM*ep96x96x96 + cp96;
}

UINT
do_move_COR_STAGE1_STM (UINT idx, int move_code)
{
	CubeStage1 cube1;
	cube1.m_co = idx;
	cube1.m_edge_ud_combo8 = 0;
	cube1.do_move (move_code);
	return cube1.m_co;
}

UINT
do_move_EDGE_STAGE1_STM (UINT idx, int move_code)
{
	CubeStage1 cube1;
	cube1.m_co = 0;
	cube1.m_edge_ud_combo8 = idx;
	cube1.do_move (move_code);
	return cube1.m_edge_ud_combo8;
}

UINT
do_move_CEN_STAGE2_STM (UINT idx, int move_code)
{
	return stage2_cen_move_fast (idx, move_code);
}

UINT
do_move_CENF_STAGE2_STM (UINT idx, int move_code)
{
	return move_table_cenSTAGE2[idx][move_code];
}

UINT
do_move_EDGE_STAGE2_STM (UINT idx, int move_code)
{
	return move_table_edgeSTAGE2[idx][move_code];
}

UINT
do_move_EDGCENF_STAGE2_STM (UINT idx, int move_code)
{
	UINT edg = idx % N_STAGE2_EDGE_CONFIGS;
	UINT cen = idx / N_STAGE2_EDGE_CONFIGS;
	return N_STAGE2_EDGE_CONFIGS*move_table_cenSTAGE2[cen][move_code] + move_table_edgeSTAGE2[edg][move_code];
}

UINT
do_move_CEN_STAGE3_STM (UINT idx, int move_code)
{
	CubeStage3 cube1;
	cube1.m_centerLR = idx;
	cube1.m_edge = 0;
	cube1.m_edge_odd = false;
	cube1.do_move (move_code);
	return cube1.m_centerLR;
}

UINT
do_move_EDGE_STAGE3_STM (UINT idx, int move_code)
{
	CubeStage3 cube1;
	cube1.m_centerLR = 0;
	cube1.m_edge = idx % N_STAGE3_EDGE_CONFIGS;
	cube1.m_edge_odd = (idx >= N_STAGE3_EDGE_CONFIGS);
	cube1.do_move (move_code);
	UINT x = cube1.m_edge;
	if (cube1.m_edge_odd) {
		x += N_STAGE3_EDGE_CONFIGS;
	}
	return x;
}

UINT
do_move_CENCOR_STAGE4_STM (UINT idx, int move_code)
{
	UINT centerUD = move_table_cenSTAGE4[idx % N_STAGE4_CENTER_CONFIGS][move_code];
	UINT corner = move_table_cornerSTAGE4[idx / N_STAGE4_CENTER_CONFIGS][move_code];
	return N_STAGE4_CENTER_CONFIGS*corner + centerUD;
}

UINT
do_move_EDGE_STAGE4_STM (UINT idx, int move_code)
{
	CubeStage4 cube1;
	cube1.m_centerUD = 0;
	cube1.m_corner = 0;
	cube1.m_edge = idx;
	cube1.do_move (move_code);
	return cube1.m_edge;
}

UINT
do_move_EDGCEN_STAGE4_STM (UINT idx, int move_code)
{
	CubeStage4 cube1;
	cube1.m_centerUD = idx % N_STAGE4_CENTER_CONFIGS;
	cube1.m_corner = 0;
	cube1.m_edge = idx / N_STAGE4_CENTER_CONFIGS;
	cube1.do_move (move_code);
	return N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
}

int
prune_funcCOR_STAGE1 (const CubeStage1& cube1)
{
	return get_dist_4bit (cube1.m_co, &prune_table_cor1[0]);
}

int
prune_funcEDGE_STAGE1 (const CubeStage1& cube1)
{
	return get_dist_4bit (cube1.m_edge_ud_combo8, &prune_table_edg1[0]);
}

int
prune_funcCEN_STAGE2 (const CubeStage2& cube2)
{
	UINT clocf = 0;
	UINT clocb = 0;
	stage2_cen_to_cloc4s (cube2.m_centerFB, &clocf, &clocb);
	UINT d1 = get_dist_4bit (clocf, &prune_table_cen2[0]);
	UINT d2 = get_dist_4bit (clocb, &prune_table_cen2[0]);
	if (d2 >= d1) {
		return d2;
	}
	return d1;
}

int
prune_funcEDGE_STAGE2 (const CubeStage2& cube2)
{
	return get_dist_4bit (cube2.m_edge, &prune_table_edg2[0]);
}

int
prune_funcEDGCEN_STAGE2 (const CubeStage2& cube2)
{
	UINT clocf = 0;

	UINT clocb = 0;
	stage2_cen_to_cloc4s (cube2.m_centerFB, &clocf, &clocb);
	UINT d1 = get_dist_4bit (N_STAGE2_EDGE_CONFIGS*clocf + cube2.m_edge, &prune_table_edgcen2[0]);
	UINT d2 = get_dist_4bit (N_STAGE2_EDGE_CONFIGS*clocb + cube2.m_edge, &prune_table_edgcen2[0]);
	if (d2 >= d1) {
		return d2;
	}
	return d1;
}

int
prune_funcCEN_STAGE3 (const CubeStage3& cube1)
{
	return get_dist_4bit (cube1.m_centerLR, &prune_table_cen3[0]);
}

int
prune_funcEDGE_STAGE3 (const CubeStage3& cube1)
{
	UINT idx = cube1.m_edge;
	if (cube1.m_edge_odd) {
		idx += N_STAGE3_EDGE_CONFIGS;
	}
	return get_dist_4bit (idx, &prune_table_edg3[0]);
}

int
prune_funcCENCOR_STAGE4 (const CubeStage4& cube1)
{
	UINT idx = N_STAGE4_CENTER_CONFIGS*cube1.m_corner + cube1.m_centerUD;
	return get_dist_4bit (idx, &prune_table_cencor4[0]);
}

int
prune_funcEDGE_STAGE4 (const CubeStage4& cube1)
{
	return get_dist_4bit (cube1.m_edge, &prune_table_edg4[0]);
}

int
prune_funcEDGCEN_STAGE4 (const CubeStage4& cube1)
{
	UINT idx = N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
	return get_dist_4bit (idx, &prune_table_edgcen4[0]);
}

int
prune_funcCENCOR_STAGE5 (const CubeSqsCoord& cube1)
{
	UINT idx = N_SQS_CORNER_PERM*cube1.m_cen12x12x12 + cube1.m_cp96;
	return get_dist_4bit (idx, &prune_table_cencor5[0]);
}

int
prune_funcEDGE_STAGE5 (const CubeSqsCoord& cube1)
{
	return get_dist_4bit (cube1.m_ep96x96x96, &prune_table_edg5[0]);
}

int
prune_funcEDGCOR_STAGE5 (const CubeSqsCoord& cube1)
{
	UINT idx = N_SQS_CORNER_PERM*cube1.m_ep96x96x96 + cube1.m_cp96;
	return get_dist_4bit (idx, &prune_table_edgcor5[0]);
}
#endif

#ifdef DO_SOLVE_STAGE4
void
splitup_fileSTAGE4 ()
{
	static unsigned char file_buffer[N_STAGE4_CENTER_CONFIGS*N_STAGE4_EDGE_CONFIGS];
	UINT i1, i, edge;
	UINT j, k;
	int dist = 17;
	int metric = 0;
	const UINT N_FILE_SIZE = static_cast<int>(N_STAGE4);
	const int N_BUFFER_SIZE = static_cast<int>(N_STAGE4_CENTER_CONFIGS*N_STAGE4_EDGE_CONFIGS);
	char fname1[64];
	char fname2[64];
	sprintf (&fname1[0], "H:\\Revenge\\stage4_%s_dist_%02d.rbk", metric_names[metric], dist);
	FILE* f1 = NULL;
	f1 = fopen (&fname1[0], "rb");
	if (f1 == NULL) {
		printf ("could not open '%s'\n", &fname1[0]);
		exit (1);
	}
	for (i1 = 0; i1 < 6; ++i1) {
		sprintf (&fname2[0], "H:\\Revenge\\stage4_%s_%d_dist_%02d.rbk", metric_names[metric], i1, dist);
		FILE* f2 = NULL;
		f2 = fopen (&fname2[0], "wb");
		if (f2 == NULL) {
			printf ("Could not create '%s'\n", &fname2[0]);
			exit (1);
		}

		for (i = 0; i < 70; ++i) {
			int n = fread (&file_buffer[0], 1, N_BUFFER_SIZE, f1);
			if (n != N_BUFFER_SIZE) {
				printf ("File read error in '%s'.\n", &fname1[0]);
				fclose (f1);
				fclose (f2);
				return;
			}
			n = fwrite (&file_buffer[0], 1, N_BUFFER_SIZE, f2);
			if (n != N_BUFFER_SIZE) {
				printf ("File write error in '%s'.\n", &fname2[0]);
			}
		}
		fclose (f2);
	}
	fclose (f1);
}
#endif

#ifdef DO_SOLVE_SQS
#ifdef USE_CUBE_LIST_TABLE
void
write_2bit_fileSQS (int dist, int metric, UINT* pcube_list)
{
	UINT i;
	UINT j, k;
	const int N_FILE_SIZE = N_SQS/4;
	const int N_BUFFER_SIZE = N_SQS_CORNER_PERM*N_SQS_CENTER_PERM/4;
	const int N_BUFFERS = N_FILE_SIZE/N_BUFFER_SIZE;
	char fname[64];
	TableIndex ti;
	sprintf (&fname[0], "H:\\Revenge\\squares_%s_distm4_%02d.rbk", metric_names[metric], dist);
	FILE* f = NULL;

	f = fopen (&fname[0], "wb");
	if (f == NULL) {
		printf ("could not create '%s'\n", &fname[0]);
		exit (1);
	}

#ifdef USE_TITLE_BAR
	if (true) {
		char str[48];
		sprintf (&str[0], "title writing 2bit file %u", dist);
		system (&str[0]);
	}
#endif
	for (i = 0; i < N_BUFFERS; ++i) {
		memset (&file_buffer[0], 0, N_BUFFER_SIZE);
		for (j = 0; j < N_BUFFER_SIZE; ++j) {
			UBYTE u = 0;
			for (k = 0; k < 4; ++k) {
				UINT idx = 4*N_BUFFER_SIZE*i + 4*j + k;
				ti.init (idx);
				int d = ti.get_value (&cube_list[0]);
				d &= 0x3;	//to be safe, shouldn't affect value
				u |= d << (2*k);
			}
			file_buffer[j] = u;
		}
		int n = fwrite (&file_buffer[0], 1, N_BUFFER_SIZE, f);
		if (n != N_BUFFER_SIZE) {
			printf ("file write error in '%s'.\n", &fname[0]);
		}
	}
	fclose (f);
}
#endif
#endif

void
CubeState::init ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		m_edge[i] = i;
	}
	for (i = 0; i < 8; ++i) {
		m_cor[i] = i;
	}
	for (i = 0; i < 24; ++i) {
		m_cen[i] = i/4;
	}
	m_distance = 255;
}

void
CubeState::init_super ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		m_edge[i] = i;
	}
	for (i = 0; i < 8; ++i) {
		m_cor[i] = i;
	}
	for (i = 0; i < 24; ++i) {
		m_cen[i] = i;
	}
	m_distance = 255;
}

bool
CubeState::compare (const CubeState& cube2) const
{
	int i;
	for (i = 0; i < 24; ++i) {
		if (m_edge[i] != cube2.m_edge[i]) {
			return false;
		}
		if (m_cen[i] != cube2.m_cen[i]) {
			return false;
		}
	}
	for (i = 0; i < 8; ++i) {
		if (m_cor[i] != cube2.m_cor[i]) {
			return false;
		}
	}
	return true;
}

void
CubeState::init_sqs ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		m_edge[i] = i;
	}
	for (i = 0; i < 8; ++i) {
		m_cor[i] = i;
	}
	for (i = 0; i < 24; ++i) {
		int x1 = i/8;
		int x2 = ((i & 0x2) == 0 ? 0 : 1);		//WRONG... we use regular init() for squares
		m_cen[i] = 2*x1 + x2;
	}
	m_distance = 255;
}

void
CubeState::do_move (int move_code)
{
	int i;
	CubeState result_cube = *this;	//fast copy to initialize result cube
	rotate_sliceEDGE (move_code, *this, &result_cube);
	rotate_sliceCORNER (move_code, *this, &result_cube);
	rotate_sliceCENTER (move_code, *this, &result_cube);
	*this = result_cube;
}

void

CubeState::compose_edge (const CubeState& cs1, const CubeState& cs2)
{
	int i;
	*this = cs1;
	for (i = 0; i < 24; ++i) {
		int j = cs2.m_edge[i];
		if (j >= 24) {
			printf ("compose_edge: CubeState error\n");
			exit (1);
		}
		m_edge[i] = cs1.m_edge[j];
	}
}

void
CubeState::compose_corner (const CubeState& cs1, const CubeState& cs2)
{
	//This function assumes corners are oriented, and the "squares" numbering convention is used.
	//So positions are restricted to the group: <U,D,L^2,R^2,F^2,B^2>
	int i;
	*this = cs1;
	for (i = 0; i < 8; ++i) {
		int j = cs2.m_cor[i];
		if (j >= 8) {
			printf ("compose_corner: CubeState error\n");
			exit (1);
		}
		m_cor[i] = cs1.m_cor[j];
	}
}

void
CubeState::invert_fbcen ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		if (m_cen[i] >= 4) {
			m_cen[i] ^= 1;
		}
	}
}

bool
CubeState::edgeUD_parity_odd () const
{
	int i, j;
	int parity = 0;
	Face t[16];

	for (i = 0; i < 16; ++i) {
		t[i] = m_edge[i];
	}
	for (i = 0; i < 15; ++i) {
		if (t[i] == i) {
			continue;
		}
		for (j = i + 1; j < 16; ++j) {
			if (t[j] == i) {
				//"swap" the i & j elements, but don't bother updating the "i"-element
				//as it isn't needed anymore.
				t[j] = t[i];
			}
		}
		parity ^= 1;
	}
	return parity != 0;
}

bool
CubeState::check () const
{
	int i;
	UINT bitmask = 0;
	for (i = 0; i < 24; ++i) {
		if (m_edge[i] >= 24) {
			return false;
		}
		if ((bitmask & (1 << m_edge[i])) != 0) {
			return false;
		}
		bitmask |= (1 << m_edge[i]);
	}
	bitmask = 0;
	for (i = 0; i < 24; ++i) {
		if (m_cen[i] >= 24) {
			return false;
		}
		if ((bitmask & (1 << m_edge[i])) != 0) {
			return false;
		}
		bitmask |= (1 << m_edge[i]);
	}
	bitmask = 0;
	for (i = 0; i < 8; ++i) {
		if (m_cor[i] >= 8) {	//squares case, corners must be oriented.
			return false;
		}
		if ((bitmask & (1 << m_edge[i])) != 0) {
			return false;
		}
		bitmask |= (1 << m_edge[i]);
	}
	return true;
}

void
CubeState::print_super () const
{
	int i;
	printf ("E");
	for (i = 0; i < 24; ++i) {
		printf (" %2d", m_edge[i]);
	}
	printf (" C");
	for (i = 0; i < 8; ++i) {
		printf (" %2d%c", m_cor[i] & 0x7, "o+-"[m_cor[i] >> 3]);
	}
	printf (" M");
	for (i = 0; i < 24; ++i) {
		printf (" %2d", m_cen[i]);
	}
	printf ("\n");
}

void
CubeCoord::init ()
{
	m_cen_fb = 0;
	m_cen_ud = 0;
	m_cen_lr = 0;
	m_co = 0;
	m_cp = 0;
	this->m_ep1 = 0;
	this->m_ep2fb = 0;
	this->m_ep2lr = 0;
	this->m_ep2ud = 0;
	m_distance = 255;
}

bool
CubeCoord::compare (const CubeCoord& cube2) const
{
	return m_ep1 == cube2.m_ep1 && m_ep2fb == cube2.m_ep2fb && m_ep2lr == cube2.m_ep2lr &&
		m_ep2ud == cube2.m_ep2ud && m_cp == cube2.m_cp && m_co == cube2.m_co &&
		m_cen_fb == cube2.m_cen_fb && m_cen_lr == cube2.m_cen_lr && m_cen_ud == cube2.m_cen_ud;
}

#ifdef CLOC_INIT
void
CubeCoord::set_from_std_cube (const CubeState& cube1)
{
	int i, count;
	Face t[8];
	count = 0;
	for (i = 0; i < 24 && count < 3; ++i) {
		if (cube1.m_edge[i] == 0) {
			t[0] = i;
			++count;
		}
		if (cube1.m_edge[i] == 8) {
			t[1] = i;
			++count;
		}
		if (cube1.m_edge[i] == 16) {
			t[2] = i;
			++count;
		}
	}
	m_ep1 = perm_n_pack2 (24, 3, &t[0]);
	count = 0;
	for (i = 0; i < 24 && count < 7; ++i) {
		if (cube1.m_edge[i] >= 1 && cube1.m_edge[i] <= 7) {
			t[cube1.m_edge[i] - 1] = i;
			++count;
		}
	}
	m_ep2lr = perm_n_pack2 (24, 7, &t[0]);
	count = 0;
	for (i = 0; i < 24 && count < 7; ++i) {
		if (cube1.m_edge[i] >= 9 && cube1.m_edge[i] <= 15) {
			t[cube1.m_edge[i] - 9] = i;
			++count;
		}
	}
	m_ep2fb = perm_n_pack2 (24, 7, &t[0]);
	count = 0;
	for (i = 0; i < 24 && count < 7; ++i) {
		if (cube1.m_edge[i] >= 17 && cube1.m_edge[i] <= 23) {
			t[cube1.m_edge[i] - 17] = i;
			++count;
		}
	}
	m_ep2ud = perm_n_pack2 (24, 7, &t[0]);

	count = 0;
	int count2 = 0;
	for (i = 0; i < 24 && count + count2 < 8; ++i) {
		if (cube1.m_cen[i] == 0) {
			t[count++] = i;
		}
		if (cube1.m_cen[i] == 1) {
			t[4 + count2++] = i;
		}
	}
	UINT cloc_u = c4_to_cloc[24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3]];
	UINT cloc_d = c4_to_cloc[24*24*24*t[4] + 24*24*t[5] + 24*t[6] + t[7]];
	this->m_cen_ud = N_CENTER_COMBO4*cloc_u + cloc_d;

	count = 0;
	count2 = 0;
	for (i = 0; i < 24 && count + count2 < 8; ++i) {
		if (cube1.m_cen[i] == 2) {
			t[count++] = i;
		}
		if (cube1.m_cen[i] == 3) {
			t[4 + count2++] = i;
		}
	}
	UINT cloc_l = c4_to_cloc[24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3]];
	UINT cloc_r = c4_to_cloc[24*24*24*t[4] + 24*24*t[5] + 24*t[6] + t[7]];
	this->m_cen_lr = N_CENTER_COMBO4*cloc_l + cloc_r;

	count = 0;
	count2 = 0;
	for (i = 0; i < 24 && count + count2 < 8; ++i) {
		if (cube1.m_cen[i] == 4) {
			t[count++] = i;
		}
		if (cube1.m_cen[i] == 5) {
			t[4 + count2++] = i;
		}
	}
	UINT cloc_f = c4_to_cloc[24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3]];
	UINT cloc_b = c4_to_cloc[24*24*24*t[4] + 24*24*t[5] + 24*t[6] + t[7]];
	this->m_cen_fb = N_CENTER_COMBO4*cloc_f + cloc_b;

	UINT orientc = 0;
	for (i = 0; i < 7; ++i) {	//don't want 8th corner orientation
		orientc = 3*orientc + (cube1.m_cor[i] >> 3);
	}
	this->m_co = orientc;
	for (i = 0; i < 8; ++i) {
		t[i] = cube1.m_cor[i] & 0x7;
	}
	this->m_cp = perm_n_pack (8, &t[0]);
}

void
CubeCoord::convert_to_std_cube (CubeState* result_cube) const
{
	int i, count;
	UINT bm1, bm2;
	Face t[8];
	count = 0;

	perm_n_unpack2 (24, 3, m_ep1, &t[0]);
	result_cube->m_edge[t[0]] = 0;
	result_cube->m_edge[t[1]] = 8;
	result_cube->m_edge[t[2]] = 16;
	perm_n_unpack2 (24, 7, 	m_ep2lr, &t[0]);
	for (i = 0; i < 7; ++i) {
		result_cube->m_edge[t[i]] = i + 1;
	}
	perm_n_unpack2 (24, 7, 	m_ep2fb, &t[0]);
	for (i = 0; i < 7; ++i) {
		result_cube->m_edge[t[i]] = i + 9;
	}
	perm_n_unpack2 (24, 7, 	m_ep2ud, &t[0]);
	for (i = 0; i < 7; ++i) {
		result_cube->m_edge[t[i]] = i + 17;
	}

	UINT cloc_u = m_cen_ud / N_CENTER_COMBO4;
	UINT cloc_d = m_cen_ud % N_CENTER_COMBO4;
	UINT u_bm = cloc_to_bm[cloc_u];
	UINT d_bm = cloc_to_bm[cloc_d];

	UINT cloc_l = m_cen_lr / N_CENTER_COMBO4;
	UINT cloc_r = m_cen_lr % N_CENTER_COMBO4;
	UINT l_bm = cloc_to_bm[cloc_l];
	UINT r_bm = cloc_to_bm[cloc_r];

	UINT cloc_f = m_cen_fb / N_CENTER_COMBO4;
	UINT cloc_b = m_cen_fb % N_CENTER_COMBO4;
	UINT f_bm = cloc_to_bm[cloc_f];
	UINT b_bm = cloc_to_bm[cloc_b];

	UINT bit = 1;
	for (i = 0; i < 24; ++i) {
		if ((u_bm & bit) != 0) {
			result_cube->m_cen[i] = 0;
		} else if ((d_bm & bit) != 0) {
			result_cube->m_cen[i] = 1;
		} else if ((l_bm & bit) != 0) {
			result_cube->m_cen[i] = 2;
		} else if ((r_bm & bit) != 0) {
			result_cube->m_cen[i] = 3;
		} else if ((f_bm & bit) != 0) {
			result_cube->m_cen[i] = 4;
		} else if ((b_bm & bit) != 0) {
			result_cube->m_cen[i] = 5;
		} else {
			printf ("error: inconsistent center bitmaps.\n");
			exit (1);
		}
		bit <<= 1;
	}

	perm_n_unpack (8, m_cp, &(result_cube->m_cor[0]));
	UINT orientc = m_co;
	UINT orientcmod3 = 0;
	for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
		Face fo = orientc % 3;
		result_cube->m_cor[i] |= (fo << 3);
		orientcmod3 += fo;
		orientc /= 3;
	}
	result_cube->m_cor[7] |= (((24 - orientcmod3) % 3) << 3);
}
#endif

bool
CubeSqsCoord::compare (const CubeSqsCoord& cube2) const
{
	return m_ep96x96x96 == cube2.m_ep96x96x96 && m_cp96 == cube2.m_cp96 &&
		m_cen12x12x12 == cube2.m_cen12x12x12;
}

int
CubeSqsCoord::cmp (const CubeSqsCoord& cube2) const
{
	if (m_ep96x96x96 < cube2.m_ep96x96x96) {
		return -1;
	}
	if (m_ep96x96x96 > cube2.m_ep96x96x96) {
		return 1;
	}
	if (m_cp96 < cube2.m_cp96) {
		return -1;
	}
	if (m_cp96 > cube2.m_cp96) {
		return 1;
	}
	if (m_cen12x12x12 < cube2.m_cen12x12x12) {
		return -1;
	}
	if (m_cen12x12x12 > cube2.m_cen12x12x12) {
		return 1;
	}
	return 0;
}

void
print_move_list (int count, const int* move_list)
{
	int j;
	if (count >= 0) {
		printf ("[%2d] ", count);
		for (j = 0; j < count; ++j) {
			printf (" %s", move_strings[move_list[j]]);
		}
	} else {
		printf ("[Did not solve]");
	}
}

void
disp_cycles (int n, const Face* elements)
{
	int i;
	bool xx[12];
	Face cycle[12];
	int len = 0;

	if (n > 12 || n < 1) {
		printf ("bad argument\n");
		return;
	}
	for (i = 0; i < 12; ++i) {
		xx[i] = false;
	}
	for (i = 0; i < n-1; ++i) {
		if (! xx[i]) {
			xx[i] = true;
			int next = elements[i];
			if (next != i) {
				printf ("(%2d", i);
				while (next != i) {
					xx[next]= true;
					printf (" %2d", next);
					next = elements[next];
				}
				printf (") ");
			}
		}
	}
}

void
printtime ()
{
	time_t timeval;
	struct tm* thetime;
	time (&timeval);
	thetime = localtime (&timeval);
	printf ("Time: %02d:%02d:%02d\n", thetime->tm_hour, thetime->tm_min, thetime->tm_sec);
}

int
countbits (UINT x)
{
	UINT x2 = ((x >> 1) & 0x55555555) + (x & 0x55555555);
	UINT x4 = ((x2 >> 2) & 0x33333333) + (x2 & 0x33333333);
	UINT x8 = ((x4 >> 4) & 0x0F0F0F0F) + (x4 & 0x0F0F0F0F);
	return static_cast<int>(x8) % 255;
}

//swapbits () - return a value with two bits interchanged.
//x = input value
//b = value (bit mask) specifying the two bits to be swapped (must have countbits (b) == 2)
UINT
swapbits (UINT x, UINT b)
{
	UINT x2 = x & b;
	if (x2 == 0 || x2 == b) {
		return x;
	}
	return x ^ b;
}

int
find_next_0bit (UINT bm, int b)
{
	int i;
	for (i = b + 1; b < 32; ++i) {
		if ((bm & (1 << i)) == 0) {
			return i;
		}
	}
	return 32;
}

int
random (int n)
{
	return rand() % n;
}

