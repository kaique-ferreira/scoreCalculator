package com.aruana.scorecalculator

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScoreResult(var maxScore: Int, var actualScore: Int, var percentage: Double = 0.toDouble()) : Parcelable {

    fun sum(scoreResult: ScoreResult): ScoreResult {

        return ScoreResult(
                actualScore = (this.actualScore + scoreResult.actualScore),
                maxScore = (this.maxScore + scoreResult.maxScore),
                percentage = 0.toDouble()
        )
    }

    fun multiply(weight: Int?): ScoreResult {

        val actualWeight = if (weight == null) {
            1
        } else {
            weight
        }

        return ScoreResult(
                actualScore = this.actualScore * actualWeight,
                maxScore = this.maxScore * actualWeight,
                percentage = 0.toDouble()
        )
    }

    override fun toString(): String {
        return "- Max inspection score: $maxScore\n" +
                "- Actual inspection score: $actualScore\n" +
                "- Result: $percentage"
    }
}
