package com.pointlessapps.dartify.local.datasource.game.x01.checkout

import com.pointlessapps.dartify.datasource.game.x01.checkout.CheckoutDataSource
import com.pointlessapps.dartify.datasource.game.x01.checkout.model.*

internal class LocalCheckoutDataSource : CheckoutDataSource {

    private val doubleOutCheckouts = listOf(
        170 to listOf(20.T, 20.T, 25.D),    167 to listOf(20.T, 19.T, 25.D),
        164 to listOf(20.T, 18.T, 25.D),    161 to listOf(20.T, 17.T, 25.D),
        160 to listOf(20.T, 20.T, 20.D),    158 to listOf(20.T, 20.T, 19.D),
        157 to listOf(20.T, 19.T, 20.D),    156 to listOf(20.T, 20.T, 18.D),
        155 to listOf(20.T, 19.T, 19.D),    154 to listOf(20.T, 18.T, 20.D),
        153 to listOf(20.T, 19.T, 18.D),    152 to listOf(20.T, 20.T, 16.D),
        151 to listOf(20.T, 17.T, 20.D),    150 to listOf(20.T, 18.T, 18.D),
        149 to listOf(20.T, 19.T, 16.D),    148 to listOf(20.T, 16.T, 20.D),
        147 to listOf(20.T, 17.T, 18.D),    146 to listOf(20.T, 18.T, 16.D),
        145 to listOf(20.T, 15.T, 20.D),    144 to listOf(20.T, 20.T, 12.D),
        143 to listOf(20.T, 17.T, 16.D),    142 to listOf(20.T, 14.T, 20.D),
        141 to listOf(20.T, 19.T, 12.D),    140 to listOf(20.T, 20.T, 10.D),
        140 to listOf(20.T, 16.T, 16.D),    139 to listOf(20.T, 13.T, 20.D),
        139 to listOf(19.T, 14.T, 20.D),    138 to listOf(20.T, 18.T, 12.D),
        137 to listOf(20.T, 19.T, 10.D),    137 to listOf(19.T, 16.T, 16.D),
        136 to listOf(20.T, 20.T, 8.D),     135 to listOf(20.T, 17.T, 12.D),
        134 to listOf(20.T, 14.T, 16.D),    133 to listOf(20.T, 19.T, 8.D),
        132 to listOf(25.D, 25.D, 16.D),    131 to listOf(19.T, 14.T, 16.D),
        130 to listOf(20.T, 20.T, 5.D),     130 to listOf(20.T, 20.S, 25.D),
        129 to listOf(19.T, 16.T, 12.D),    128 to listOf(18.T, 14.T, 16.D),
        127 to listOf(20.T, 17.T, 8.D),     126 to listOf(19.T, 19.T, 6.D),
        125 to listOf(20.T, 15.T, 10.D),    124 to listOf(20.T, 16.T, 8.D),
        123 to listOf(19.T, 16.T, 9.D),     122 to listOf(18.T, 18.T, 7.D),
        121 to listOf(20.T, 11.T, 14.D),    120 to listOf(20.T, 20.S, 20.D),
        119 to listOf(19.T, 12.T, 13.D),    118 to listOf(20.T, 18.S, 20.D),
        117 to listOf(20.T, 17.S, 20.D),    116 to listOf(19.T, 19.S, 20.D),
        115 to listOf(20.T, 15.S, 20.D),    114 to listOf(20.T, 14.S, 20.D),
        113 to listOf(20.T, 13.S, 20.D),    112 to listOf(20.T, 12.S, 20.D),
        111 to listOf(20.T, 19.S, 16.D),    110 to listOf(20.T, 18.S, 16.D),
        109 to listOf(20.T, 17.S, 16.D),    108 to listOf(20.T, 16.S, 16.D),
        107 to listOf(19.T, 18.S, 16.D),    106 to listOf(20.T, 14.S, 16.D),
        105 to listOf(19.T, 16.S, 16.D),    104 to listOf(18.T, 18.S, 16.D),
        103 to listOf(19.T, 14.S, 16.D),    102 to listOf(20.T, 10.S, 16.D),
        101 to listOf(20.T, 9.S, 16.D),     100 to listOf(20.T, 20.D),
        99 to listOf(19.T, 10.S, 16.D),     98 to listOf(20.T, 19.D),
        97 to listOf(19.T, 20.D),   96 to listOf(20.T, 18.D),   95 to listOf(19.T, 19.D),
        94 to listOf(18.T, 20.D),   93 to listOf(19.T, 18.D),   92 to listOf(20.T, 16.D),
        91 to listOf(17.T, 20.D),   90 to listOf(18.T, 18.D),   89 to listOf(19.T, 16.D),
        88 to listOf(20.T, 14.D),   87 to listOf(17.T, 18.D),   86 to listOf(18.T, 16.D),
        85 to listOf(15.T, 20.D),   84 to listOf(20.T, 12.D),   83 to listOf(17.T, 16.D),
        82 to listOf(14.T, 20.D),   81 to listOf(19.T, 12.D),   80 to listOf(20.T, 10.D),
        79 to listOf(13.T, 20.D),   78 to listOf(18.T, 12.D),   77 to listOf(19.T, 10.D),
        76 to listOf(20.T, 8.D),    75 to listOf(17.T, 12.D),   74 to listOf(14.T, 16.D),
        73 to listOf(19.T, 8.D),    72 to listOf(16.T, 12.D),   71 to listOf(13.T, 16.D),
        70 to listOf(10.T, 20.D),   69 to listOf(15.T, 12.D),   68 to listOf(20.T, 4.D),
        67 to listOf(17.T, 8.D),    66 to listOf(10.T, 18.D),   65 to listOf(19.T, 4.D),
        64 to listOf(16.T, 8.D),    63 to listOf(13.T, 12.D),   62 to listOf(10.T, 16.D),
        61 to listOf(15.T, 8.D),    60 to listOf(20.S, 20.D),   59 to listOf(19.S, 20.D),
        58 to listOf(18.S, 20.D),   57 to listOf(17.S, 20.D),   56 to listOf(16.S, 20.D),
        55 to listOf(15.S, 20.D),   54 to listOf(14.S, 20.D),   53 to listOf(13.S, 20.D),
        52 to listOf(12.S, 20.D),   51 to listOf(11.S, 20.D),   50 to listOf(10.S, 20.D),
        50 to listOf(25.D),         49 to listOf(9.S, 20.D),    48 to listOf(8.S, 20.D),
        47 to listOf(15.S, 16.D),   46 to listOf(10.S, 18.D),   45 to listOf(13.S, 16.D),
        44 to listOf(12.S, 16.D),   43 to listOf(11.S, 16.D),   42 to listOf(10.S, 16.D),
        41 to listOf(9.S, 16.D),    40 to listOf(20.D),         39 to listOf(7.S, 16.D),
        38 to listOf(19.D),         37 to listOf(5.S, 16.D),    36 to listOf(18.D),
        35 to listOf(3.S, 16.D),    34 to listOf(17.D),         33 to listOf(1.S, 16.D),
        32 to listOf(16.D),         31 to listOf(7.S, 12.D),    30 to listOf(15.D),
        29 to listOf(5.S, 12.D),    28 to listOf(14.D),         27 to listOf(3.S, 12.D),
        26 to listOf(13.D),         25 to listOf(1.S, 12.D),    24 to listOf(12.D),
        23 to listOf(7.S, 8.D),     22 to listOf(11.D),         21 to listOf(5.S, 8.D),
        20 to listOf(10.D),         19 to listOf(3.S, 8.D),     18 to listOf(9.D),
        17 to listOf(9.S, 4.D),     16 to listOf(8.D),          15 to listOf(7.S, 4.D),
        14 to listOf(7.D),          13 to listOf(5.S, 4.D),     12 to listOf(6.D),
        11 to listOf(3.S, 4.D),     10 to listOf(5.D),          9 to listOf(1.S, 4.D),
        8 to listOf(4.D),           7 to listOf(3.S, 2.D),      6 to listOf(3.D),
        5 to listOf(1.S, 2.D),      4 to listOf(2.D),           3 to listOf(1.S, 1.D),
        2 to listOf(1.D),
    )

    override fun getCheckoutFor(
        turnScoreLeft: Int,
        scoreLeft: Int,
        numberOfThrows: Int,
    ): List<Score>? {
        val fullMatch = doubleOutCheckouts.find { (score, checkout) ->
            score == scoreLeft && checkout.size <= numberOfThrows
        }?.second

        val partialMatch = doubleOutCheckouts
            .asSequence()
            .filter { it.first == turnScoreLeft }
            .sortedByDescending { it.second.size }
            .map { it.second.takeLast(numberOfThrows) }
            .filter { it.sumOf(Score::score) == scoreLeft }
            .firstOrNull()

        return fullMatch ?: partialMatch
    }
}
