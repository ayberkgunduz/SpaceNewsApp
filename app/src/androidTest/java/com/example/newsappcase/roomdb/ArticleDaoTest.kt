package com.example.newsappcase.roomdb

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.newsappcase.db.ArticleDatabase
import com.example.newsappcase.db.ArticleDoa
import com.example.newsappcase.getOrAwaitValueTest
import com.example.newsappcase.model.Article
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@SmallTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class ArticleDaoTest {

    @get:Rule
    var instantTaskExecuterRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("testDatabase")
    lateinit var database: ArticleDatabase

    private lateinit var articleDoa: ArticleDoa

    @Before
    fun setup() {
        hiltRule.inject()
        articleDoa = database.getArticleDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertArticleTesting() = runTest {
        val exampleArticle = Article(1,null,null,88,"space",null,"site",null,"Summary","SPACE",null,"myUrl")
        articleDoa.insert(exampleArticle)
        val list = articleDoa.getAllArticles().getOrAwaitValueTest()
        assertThat(list).contains(exampleArticle)
    }

    @Test
    fun deleteArticleTesting()= runTest{
        val exampleArticle = Article(1,null,null,88,"space",null,"site",null,"Summary","SPACE",null,"myUrl")
        articleDoa.insert(exampleArticle)
        articleDoa.deleteArticle(88)
        val list = articleDoa.getAllArticles().getOrAwaitValueTest()
        assertThat(list).doesNotContain(exampleArticle)
    }

    @Test
    fun isArticleExistTesting()= runTest{
        val exampleArticle = Article(1,null,null,88,"space",null,"site",null,"Summary","SPACE",null,"myUrl")
        articleDoa.insert(exampleArticle)
        articleDoa.isArticleExists(88)
        val isExist = articleDoa.isArticleExists(88)
        assertThat(isExist).isEqualTo(true)
    }

}