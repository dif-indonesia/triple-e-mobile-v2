package id.co.dif.base_project.utils

import id.co.dif.base_project.R
import java.util.Random
import kotlin.math.abs

class ColorGenerator private constructor(private val mColors: List<Int>) {
    private val mRandom: Random = Random(System.currentTimeMillis())

    val randomColor:  Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[abs(key.hashCode()) % mColors.size]
    }

    companion object {
        var DEFAULT: ColorGenerator? = null

        init {
            DEFAULT = create(
                listOf(
                    R.color.light_orange,
                    R.color.light_green,
                    R.color.dark_pink,
                    R.color.dark_purple,
                    R.color.light_blue,
                    R.color.alpha_60_light_purple,
                    R.color.purple,
                    R.color.teal_200,
                    R.color.light_blue
                )
            )
        }

        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}