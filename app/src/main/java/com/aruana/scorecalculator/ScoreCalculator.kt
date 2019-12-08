package com.aruana.scorecalculator

import com.aruana.scorecalculator.networking.Item
import com.aruana.scorecalculator.networking.Params
import com.aruana.scorecalculator.networking.ResponseSet

const val QUESTION_ITEM_TYPE = "question"
const val PAGE_ITEM_TYPE = "page"
const val SECTION_ITEM_TYPE = "section"
const val LIST_ITEM_TYPE = "list"

class ScoreCalculator {

    private var answers: List<ResponseSet> = emptyList()

    fun calculateScore(item: Item): ScoreResult? {
        if (item.items.isNullOrEmpty() || item.params == null || item.params.response_sets.isNullOrEmpty()) {
            return null
        }

        answers = item.params.response_sets
        return calculateScore(item.items)
    }

    private fun calculateScore(pages: List<Item>): ScoreResult {
        var result = emptyScoreResult()

        pages.forEach {
            result = result.sum(calculateRecursiveScore(it))
        }

        with(result) {
            percentage = actualScore.toDouble() / maxScore
            return this
        }
    }

    private fun calculateRecursiveScore(item: Item): ScoreResult {
        if (isPageOrSection(item)) {
            return handlePageOrSection(item)
        }

        if (isQuestion(item) && hasResponse(item, item.params)) {
            return handleQuestion(item)
        }

        return emptyScoreResult()
    }

    private fun isQuestion(item: Item) = item.type == QUESTION_ITEM_TYPE && item.items.isNullOrEmpty()

    private fun handleQuestion(question: Item): ScoreResult {
        val actualScore = getActualScoreForQuestion(getResponse(question), question.params?.response_set)

        return if (isQuestionApplicable(actualScore)) {
            ScoreResult(maxScore = getMaxScoreForQuestion(question.params?.response_set), actualScore = actualScore!!)
        } else {
            emptyScoreResult()
        }
    }

    private fun handlePageOrSection(item: Item): ScoreResult {
        var result = emptyScoreResult()

        item.items?.forEach {
            result = result.sum(calculateRecursiveScore(it))
        }

        return result.multiply(item.weight)
    }

    private fun isQuestionApplicable(actualScore: Int?) = actualScore != null

    private fun hasResponse(item: Item, params: Params?) = item.response_type == LIST_ITEM_TYPE && item.params != null && params?.response_set != null

    private fun isPageOrSection(question: Item) = question.type == PAGE_ITEM_TYPE || question.type == SECTION_ITEM_TYPE

    private fun getResponse(question: Item) = question.response.firstOrNull()

    private fun getActualScoreForQuestion(responseId: String?, responseSetId: String?): Int? {
        if (responseId == null) {
            return 0
        }

        val responseSet = answers.find { it.uuid == responseSetId }

        val response = responseSet?.responses?.find { it.uuid == responseId }

        return response?.score
    }

    private fun getMaxScoreForQuestion(responseSetId: String?): Int {
        if (responseSetId.isNullOrEmpty()) {
            return 0
        }

        val responseSet = answers.find { it.uuid == responseSetId }

        val response = responseSet?.responses?.maxBy { it.score ?: 0 }

        return response?.score ?: 0
    }

    private fun emptyScoreResult() = ScoreResult(0, 0, 0.toDouble())
}