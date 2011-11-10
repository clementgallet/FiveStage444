#ifndef CUBEPRUNINGTABLEMGR_H
#define CUBEPRUNINGTABLEMGR_H

#pragma once

#include "Constants.h"
#include "CubePruningTable.h"
#include "PruningTables.h"
#include "CubeConverter.h"
#include "CubeSqsCoord.h"
#include "CubeStage1.h"
#include "CubeStage2.h"
#include "CubeStage3.h"
#include "CubeStage4.h"
#include "CubeState.h"
#include "Tables.h"

//Class to create and clean up all pruning tables
class CubePruningTableMgr {
public:
	void init_pruning_tables (int metric);
};

UINT do_move_EDGE_STAGE1_STM (UINT idx, int move_code);
UINT do_move_COR_STAGE1_STM (UINT idx, int move_code);
UINT do_move_EDGCENF_STAGE2_STM (UINT idx, int move_code);
UINT do_move_CEN_STAGE3_STM (UINT idx, int move_code);
UINT do_move_EDGE_STAGE3_STM (UINT idx, int move_code);
UINT do_move_CENCOR_STAGE4_STM (UINT idx, int move_code);
UINT do_move_EDGCEN_STAGE4_STM (UINT idx, int move_code);
UINT do_move_CENCOR_STAGE5 (UINT idx, int sqs_move_code);
UINT do_move_EDGCOR_STAGE5 (UINT idx, int sqs_move_code);

#endif
