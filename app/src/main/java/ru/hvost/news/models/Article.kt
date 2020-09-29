package ru.hvost.news.models

import kotlin.random.Random

data class Article (
    val id: Long,
    val img: String,
    val title: String,
    val description: String,
    val category: String,
    val views: String,
    val likes: String,
    val domainIcon: String,
    val domain:String
)
{
    companion object{
        fun getTestArticlesList(): List<Article>{
            val result = mutableListOf<Article>()
            for(i in 0L until 100L){
                result.add(
                    Article(
                        id = i + 1,
                        img = "https://avatars.mds.yandex.net/get-pdb/963318/baf532b6-3a69-4ba2-ab0d-08e2fd5388ca/s600?webp=false",
                        title = listOf("Собака", "Кошка", "Псина")[Random.nextInt(2)],
                        description = "test description $i",
                        category = listOf("Собака", "Кошка", "Псина")[Random.nextInt(2)],
                        views = listOf("123", "1", "322312")[Random.nextInt(2)],
                        likes = listOf("111", "2", "3124")[Random.nextInt(2)],
                        domainIcon = "https://i.pinimg.com/736x/5e/b3/a5/5eb3a5665fe4e63f8c813a525aa3beb8.jpg",
                        domain = listOf("Собаки", "Кошки", "Птицы", "Черепахи")[Random.nextInt(4)]
                    )
                )
            }
            return result
        }
    }
}