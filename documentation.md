=========================
Description of the stages
=========================

Stage 1
-------

Goal: 
- Orient the corner cubies.
- Put the u- and d-layer edges into those two layers. (A d-layer edge may be in u layer, and a u-layer edge may be in the d layer.)

Turns allowed:

    U, U', U2, u, u', u2, D, D', D2, d, d', d2,
    L, L', L2, l, l', l2, R, R', R2, r, r', r2,
    F, F', F2, f, f', f2, B, B', B2, b, b', b2

One-time whole cube rotations allowed: 120-degree turns (either direction) about the UFL-DBR axis.

There are 3 possibilities for each corner orientation and 8 corners so 3^8 possibilities. However, when setting the orientation of 7 corners, the 8th is fixed so only 3^7 = 2,187 cases.

For the edges, we have to record the position of 8 edges among 24 slots, so C^24_8 = 735,471 cases. This coordinate is reduced by symmetry. There are 48 symmetries, which let to only 15582 cases.

The overall space of this stage is 2,187 x 15,582 = 34,077,834.

Stage 2
-------

Goal:
- Put front and back centers onto the front and back faces into one of the twelve configurations that can be solved using only half-turn moves.
- Arrange u- and d-layer edges within the u- and d-layers so that they will be in one of the 96 configurations that can be solved using only half-turn moves.

Turns allowed:

    U, U', U2, u, u', u2, D, D', D2, d, d', d2,
           L2, l, l', l2,        R2, r, r', r2,
           F2, f, f', f2,        B2, b, b', b2

One-time whole cube rotations allowed: 90-degree turn about U-D axis.

As for edges of stage 1, there are C^24_8 = 735,471 cases for storing the position of the 8 F/B centers, and we also need to keep track of which center are F and which are B, requiring another C^8_4 = 70, so a total of 735,471 x 70 = 51,482,970. However, we don't need to track the exactly colour of those centers, but only if two centers are from the same or different colours. This reduce by a factor of two, giving 735,471 x 35 = 25,741,485. This (large) coordinate is reduced by symmetry (16 symmetries), giving 1,612,515 unique coordinates.

For the edges, the total number of permutations is 8! = 40,320 and there are 96 configurations that are considered solved (square group), so the coordinate is 40,320/96 = 420.

The overall space of this stage is 1,612,515 x 420 = 677,256,300

Stage 3
-------

Goal:
- Put centers for left and right faces into the left and right faces so that they are in one of the 12 configurations that can be solved using only half-turn moves. This leaves the centers for the U and D faces arbitrarily arranged in the U and D faces.
- Put top and bottom layer edges into positions such that the U or D facelet is facing either up or down. Also, put these edges into an even permutation.

Turns allowed:

    U, U', U2,        u2, D, D', D2,        d2,
           L2,        l2,        R2,        r2,
           F2, f, f', f2,        B2, b, b', b2

For centers, this is the same as for stage 2, except that now there are only 16 remaining slots for one center, as F and B faces are filled during stage 2. Using the same principle, the center coordinate is C^16_8 x C^8_4 = 12,870 x 35 = 450,450. Using symmetry reduction (8 in this stage), we only need to store 56,980 positions.

For edges, we need to put half of the edges in half of the positions, so C^16_8 = 12,870 cases. The even permutation required gives a extra factor of 2.

The overall space of this stage is 56,980 x 12,870 x 2 = 1,466,665,200.

Stage 4
-------

Goal:
- Put corners into one of the 96 configurations that can be solved using only half-turn moves.
- Put U and D centers into one of the 12 configurations that can be solved using only half-turn moves.
- Put all U- and D-layer edges into a configuration that can be solved using only half-turn moves. This consists of 96 possible configurations for the l- and r-layer edges, and 96 for the f- and b-layer edges.

Turns allowed:

    U, U',U2, u2, D, D', D2, d2,
          L2, l2,        R2, r2,
          F2, f2,        B2, b2

The corner coordinate is exactly like the edge coordinate from stage 2, which gives 420 cases.

The remaining centers are already in the right faces, so we only need to put them in the right order: C^8_4 = 70. Using the same trick as for stage 2 and 3, we only need to keep 35 cases.

The edges are as the corners, except that there are two groups of edges so 420 x 420 = 176,400. In fact, only half of the cases happen, because of the parity condition from stage 3, so 88,200 real cases. We are doing the symmetry reduction on this coordinate (16 symmetries), which leave only 5,968 cases.

The overall size is 420 x 35 x 5,968 = 87,729,600

Stage 5
-------

Goal:
- Put all cubies into their solved position.

Turns allowed:

    U2, u2, D2, d2,
    L2, l2, R2, r2,
    F2, f2, B2, b2

There are 96 positions for corners.

Edges are like 3 independent groups of corners, so 96 x 96 x 96 = 884,736 positions. We are doing a symmetry reduction, and we use a trick to get as much as 192 different symmetries, so that this coordinate has only 7,444 positions. In addition to the usual 48 symmetries of the cube, we add 4 cube rotations (generated by x2 and y2) because the cube can be in four different solved positions. This allows us to reduce this coordinate by a factor of 48 x 4 = 192.

For centers, each pairs of opposite centers have 12 different configurations, so 12*12*12 = 1,728 positions.

The overall size is 96*7,444*1,728 = 1,234,870,272

==============
Cube structure
==============

Edges
------

There are 24 "edge" cubies, numbered 0 to 23.
The home positions of these cubies are labeled in the diagram below.
Each edge cubie has two exposed faces, so there are two faces labelled with each number.

                -------------
                |    5  1   |
                |12   U   10|
                | 8       14|
                |    0  4   |
    -------------------------------------------------
    |   12  8   |    0  4   |   14 10   |    1  5   |
    |22   L   16|16   F   21|21   R   19|19   B   22|
    |18       20|20       17|17       23|23       18|
    |    9 13   |    6  2   |   11 15   |    7  3   |
    -------------------------------------------------
                |    6  2   |
                |13   D   11|
                | 9       15|
                |    3  7   |
                -------------

Corners
-------

There are 8 "corner" cubies, numbered 0 to 7.
The home positions of these cubies are labeled in the diagram below.
Each corner cubie has three exposed faces, so there are three faces labelled with each number. Asterisks mark the primary facelet position. Orientation will be the number of clockwise rotations the primary facelet is from the primary facelet position where it is located.

               +----------+
               |*5*    *1*|
               |    U     |
               |*0*    *4*|
    +----------+----------+----------+----------+
    | 5      0 | 0      4 | 4      1 | 1      5 |
    |     L    |    F     |    R     |    B     |
    | 3      6 | 6      2 | 2      7 | 7      3 |
    +----------+----------+----------+----------+
               |*6*    *2*|
               |    D     |
               |*3*    *7*|
               +----------+

Centers
-------

There are 24 "center" cubies. They are numbered 0 to 23 as shown.

                -------------
                |           |
                |    3  1   |
                |    0  2   |
                |           |
    -------------------------------------------------
    |           |           |           |           |
    |   10  8   |   16 19   |   14 12   |   21 22   |
    |    9 11   |   18 17   |   13 15   |   23 20   |
    |           |           |           |           |
    -------------------------------------------------
                |           |
                |    6  4   |
                |    5  7   |
                |           |
                -------------


===============
Distance tables
===============

Stage 1 - 48 symmetries

                    Slice turns
              ------------------------
    distance  positions         unique
    --------  ---------         ------
       0              3              1
       1              6              1
       2            144              4
       3          2,796             66
       4         48,324          1,033
       5        745,302         15,620
       6     10,030,470        209,273
       7    103,416,912      2,155,397
       8    575,138,592     11,984,424
       9    826,559,202     17,222,730
      10     92,489,544      1,927,399
      11         43,782            916
          -------------    -----------
          1,608,475,077     33,516,864


Stage 2 - 16 symmetries

                    Slice turns
              ------------------------
    distance  positions         unique
    --------  ---------         ------
       0             12              6
       1             36              8
       2            684             54
       3          9,254            661
       4        103,998          6,785
       5      1,149,674         73,297
       6     11,929,486        750,382
       7     92,729,838      5,803,099
       8    447,778,202     27,991,967
       9  1,247,722,776     77,990,037
      10  1,930,825,644    120,695,743
      11  2,215,400,576    138,498,874
      12  2,607,462,418    163,000,022
      13  1,828,141,454    114,282,664
      14    426,682,056     26,675,281
      15      1,487,536         93,585
      16             56              5
           ------------  -------------
         10,811,423,700    675,862,470

Stage 3 - 8 symmetries

                     Slice turns
               ------------------------
    distance   positions         unique
    --------   ---------         ------
       0               6              6
       1              12              4
       2             150             28
       3           1,556            230
       4          16,310          2,185
       5         169,240         21,630
       6       1,717,460        216,142
       7      16,888,105      2,115,779
       8     155,841,738     19,496,147
       9   1,219,752,205    152,510,075
      10   5,364,611,902    670,664,810
      11   4,687,652,572    586,031,875
      12     147,926,722     18,500,776
      13           5,021            732
      14               1              1
          --------------  -------------
          11,594,583,000  1,466,665,200


Stage 4 - 16 symmetries - problem with positions, not correct.

                     Slice turns
               ------------------------
    distance   positions         unique
    --------   ---------         ------
       0              12              4
       1              24              3
       2             204             12
       3           1,280             40
       4           7,548            171
       5          40,964            899
       6         227,816          4,528
       7       1,259,844         21,918
       8       6,912,088        108,036
       9      35,259,020        534,374
      10     152,072,296      2,417,720
      11     466,530,500      8,958,735
      12     759,591,796     23,141,105
      13     738,648,672     30,826,779
      14     387,337,472     14,255,009
      15      45,079,256      1,014,899
      16         111,144          1,988
           -------------    -----------
           2,593,080,000     81,286,220

Stage 5 ("Squares Coset") - 192 symmetries

                     Slice turns
               --------------------------
    distance    positions          unique
    --------    ---------          ------
       0                4               1
       1               48               2
       2              420               7
       3            3,456              36
       4           27,168             228
       5          203,752           1,429
       6        1,451,996           9,127
       7        9,527,856          55,967
       8       56,528,036         320,517
       9      295,097,696       1,636,219
      10    1,306,291,304       7,145,262
      11    4,761,203,264      25,797,686
      12   13,820,728,272      74,257,367
      13   29,956,341,744     159,930,965
      14   43,427,866,752     231,079,243
      15   36,297,535,208     193,022,572
      16   14,711,566,720      78,368,608
      17    2,063,584,704      11,055,492
      18       59,082,112         320,252
      19           45,056             244
          ---------------   -------------
          146,767,085,568     783,001,224


