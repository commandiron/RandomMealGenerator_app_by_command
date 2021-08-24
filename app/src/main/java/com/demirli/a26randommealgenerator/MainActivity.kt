package com.demirli.a26randommealgenerator

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        get_random_meal_btn.setOnClickListener {
            val getRandomMeal = GetRandomMeal()
            val url = "https://www.themealdb.com/api/json/v1/1/random.php"
            getRandomMeal.execute(url)
        }

    }

    inner class GetRandomMeal(): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            var result = ""
            var url: URL
            var httpURLConnection: HttpURLConnection

            url = URL(params[0])
            httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            var data = inputStreamReader.read()

            while(data > 0){
                val character1 = data.toChar()
                result += character1

                data = inputStreamReader.read()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val jsonObject =JSONObject(result)
            val meals = jsonObject.getString("meals")
            val jsonArray = JSONArray(meals)

            val mealsArray = jsonArray.getString(0)
            val mealsObject = JSONObject(mealsArray)
            val strMeal = mealsObject.getString("strMeal")
            val strInstructions = mealsObject.getString("strInstructions")
            val strMealThumb =  mealsObject.getString("strMealThumb")

            var ingredientList = arrayListOf<String>()
            var ingredientMeasureList = arrayListOf<String>()
            val i = 1
            for (i in 1 until 20){
                val strMeasure = mealsObject.getString("strMeasure$i")
                val strIngredient = mealsObject.getString("strIngredient$i")
                if(strIngredient != "" && strMeasure != ""){
                    ingredientList.add(strIngredient)
                    ingredientMeasureList.add(strMeasure)
                }
            }
            val ingredientAndMeasure = ingredientMeasureList.zip(ingredientList)

            recipe_name_tv.text = strMeal
            ingredients_tv.text = "Ingredients: " + ingredientAndMeasure
            instructions_tv.text = "Instructions: " + strInstructions
            Picasso.get().load(strMealThumb).into(recipe_photo_iv)
        }
    }
}
