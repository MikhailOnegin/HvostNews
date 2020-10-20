package ru.hvost.news.models

data class Species(
    val id: Long,
    val speciesId: Int,
    val speciesName: String
) {

    companion object {

        fun getTestList(): List<Species> {
            return listOf(
                Species(1L, 1, "Кошка"),
                Species(2L, 2, "Собака"),
                Species(3L, 3, "Носорог"),
                Species(4L, 4, "Крокодил"),
                Species(5L, 5, "Крыса")
            )
        }

    }

}