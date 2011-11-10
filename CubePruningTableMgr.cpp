#include "CubePruningTableMgr.h"

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
	CubeStage3 stage3_solved;
	CubeStage4 stage4_solved;
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
			n_moves = N_STAGE2_SLICE_MOVES;
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
do_move_EDGE_STAGE1_STM (UINT idx, int move_code)
{
	CubeStage1 cube1;
	cube1.m_co = 0;
	cube1.m_edge_ud_combo8 = idx;
	cube1.do_move (move_code);
	return cube1.m_edge_ud_combo8;
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
do_move_EDGCEN_STAGE4_STM (UINT idx, int move_code)
{
	CubeStage4 cube1;
	cube1.m_centerUD = idx % N_STAGE4_CENTER_CONFIGS;
	cube1.m_corner = 0;
	cube1.m_edge = idx / N_STAGE4_CENTER_CONFIGS;
	cube1.do_move (move_code);
	return N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
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
