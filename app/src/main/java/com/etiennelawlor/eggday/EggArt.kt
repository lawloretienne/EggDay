package com.etiennelawlor.eggday

/**
 * Proportions of the generated egg art (scripts/generate_art.py), used to lay the dot-matrix
 * digits over `R.drawable.egg` in both the widget and the app. The egg drawable is a
 * 100 x 136 box: the egg plus room for its shadow.
 */
object EggArt {
    /** Egg drawable width / height. */
    const val ASPECT = 100f / 136f

    /** Digit height as a fraction of the egg drawable's height (STAMP_DIGIT_H). */
    const val DIGIT_HEIGHT = 28f / 136f

    /** Digit width / height: 5 x 7 dots. */
    const val DIGIT_ASPECT = 5f / 7f

    /** Gap between digits as a fraction of digit height (STAMP_GAP). */
    const val DIGIT_GAP = 0.16f

    /** How far below the drawable's center the number sits (STAMP_CENTER_Y 80 vs center 68). */
    const val STAMP_OFFSET = (80f - 68f) / 136f

    val DIGITS = intArrayOf(
        R.drawable.dm_0, R.drawable.dm_1, R.drawable.dm_2, R.drawable.dm_3, R.drawable.dm_4,
        R.drawable.dm_5, R.drawable.dm_6, R.drawable.dm_7, R.drawable.dm_8, R.drawable.dm_9,
    )
}
