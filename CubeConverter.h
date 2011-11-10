#ifndef CUBECONVERTER_H
#define CUBECONVERTER_H

#pragma once

#include "CubeState.h"
#include "CubeSqsCoord.h"
#include "CubeStage1.h"
#include "CubeStage2.h"
#include "CubeStage3.h"
#include "CubeStage4.h"
#include "Constants.h"
#include "Tables.h"

void convert_stage1_to_std_cube (const CubeStage1& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage1 (const CubeState& init_cube, CubeStage1* result_cube);

void convert_stage2_to_std_cube (const CubeStage2& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage2 (const CubeState& init_cube, CubeStage2* result_cube);

void convert_stage3_to_std_cube (const CubeStage3& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage3 (const CubeState& init_cube, CubeStage3* result_cube);

void convert_stage4_to_std_cube (const CubeStage4& init_cube, CubeState* result_cube);
void convert_std_cube_to_stage4 (const CubeState& init_cube, CubeStage4* result_cube);
void convert_std_cube_to_squares (const CubeState& init_cube, CubeSqsCoord* result_cube);
void pack_cubeSQS (const CubeState& cube1, CubeSqsCoord* result_cube);
UINT squares_pack_centers (const Face* arr);

#endif
