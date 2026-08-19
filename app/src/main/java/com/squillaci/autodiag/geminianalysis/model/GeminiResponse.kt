package com.squillaci.autodiag.geminianalysis.model

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)