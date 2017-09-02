package com.farifam.lovecalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
//import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine


import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.http.Headers
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var disposable: Disposable? = null

    val wikiApiServe by lazy {
        WikiApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val name=findViewById(R.id.name) as EditText;
//        val partner_name=findViewById(R.id.partner_name) as EditText;
//        val submit=findViewById(R.id.submit) as Button;
//        val image_indicator=findViewById(R.id.loading) as ImageView;
//
//        val result=findViewById(R.id.res) as LinearLayout;
//        val res_title=findViewById(R.id.res_title) as TextView;
//        val res_explanation=findViewById(R.id.res_explanation) as TextView;
        progressBar1.visibility=View.GONE;
        res.visibility=View.GONE;

        submit.setOnClickListener {
//            consumseApi(name.text.toString(), partner_name.text.toString());
            if (name.text.toString().isNotEmpty()) {
                progressBar1.visibility=View.VISIBLE;
                beginSearch(name.text.toString(), partner_name.text.toString())
            }
        }
    }

//    fun consumseApi(name: String, partner_name: String){
//
//    }

    override fun onResume() {
        super.onResume()

    }

    private fun beginSearch(fname: String, sname: String) {
        disposable = wikiApiServe.hitCountCheck(fname, sname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { data ->
                            run {
                                progressBar1.visibility=View.GONE;
                                res.visibility=View.VISIBLE;
                                res_title.text = "${data.percentage} %";
                                res_explanation.text = "${data.result}"
                            }},
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

object Model {
    data class Data(val result: String, val percentage: String)
//    data class Result(val percentage: String, val label: String)
//    data class Result(val totalhits: Int)
}

interface WikiApiService {

//    @Headers({
//        "X-Mashape-Key": "RJRlITgCSCmshYHawXRnlJFc1KH8p1f8jtujsnaIUdxXyHR1vR"
//    })
    @GET("getPercentage")
    fun hitCountCheck(@Query("fname") fname: String,
                      @Query("sname") sname: String):
            Observable<Model.Data>

    companion object {
        fun create(): WikiApiService {

//            String url="https://love-calculator.p.mashape.com/getPercentage?fname="+name1+"&sname="+name2";

            val httpClient = OkHttpClient().newBuilder();

            httpClient.addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder().addHeader("X-Mashape-Key", "your_api").build()
                chain.proceed(request)
            })

            val client = httpClient.build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://love-calculator.p.mashape.com/")
                    .client(client)
                    .build()

            return retrofit.create(WikiApiService::class.java)
        }
    }

}
