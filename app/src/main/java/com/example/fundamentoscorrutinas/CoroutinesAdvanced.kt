package com.example.fundamentoscorrutinas

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val countries = listOf("Santander","CDMX","Lima","Buenos Aires", "Monterrey")

fun main(){
   // basicChannel()
    //closeChannel()
    //produceChannel()
   // pipelines()
    bufferChannel()
}

fun bufferChannel() {
    runBlocking {
        newTopic("Buffer para channels")
        val time = System.currentTimeMillis()
        val channel = Channel<String>()
        launch {
            countries.forEach {
                delay(100)
                channel.send(it)
            }
            channel.close()
        }
        launch {
            delay(1_000)
            channel.consumeEach { println(it) }
            println("Time: ${System.currentTimeMillis() - time} ms ")
        }


        val buffertime = System.currentTimeMillis()
        val bufferchannel = Channel<String>(2)
        launch {
            countries.forEach {
                delay(100)
                bufferchannel.send(it)
            }
            bufferchannel.close()
        }
        launch {
            delay(1_000)
            bufferchannel.consumeEach { println(it) }
            println("B ->Time: ${System.currentTimeMillis() - buffertime} ms ")
        }
    }
}

fun pipelines() {
    runBlocking {
        newTopic("Pipelines")
        val citiesChannel = produceCities()
        val foodsChannel = produceFoods(citiesChannel)
        foodsChannel.consumeEach { println(it   ) }
        citiesChannel.cancel()
        foodsChannel.cancel()
        println("Todo Correcto")

    }
}
fun CoroutineScope.produceFoods(cities: ReceiveChannel<String>) : ReceiveChannel<String> = produce {
    for(city in cities){
        val food = getFoodByCity(city)
        send("$food desde $city")
    }
}

suspend fun getFoodByCity(city: String): String {
    delay(300)
    return when(city){
        "Santander" -> "Arepa"
        "CDMX" -> "Taco"
        "Lima" -> "Ceviche"
        "Buenos Aires" -> "Milanesa"
        "Monterrey" -> "Carne Asada"
        else -> "Sin datos"
    }
}

fun produceChannel() {
    runBlocking {
        newTopic("Canales y el patro producto- cosumidor")
        val names = produceCities()
        names.consumeEach { println(it) }
    }
}
fun CoroutineScope.produceCities() : ReceiveChannel<String> = produce {
    countries.forEach {
        send(it)
    }
}

fun closeChannel() {
    runBlocking {
        newTopic("Cerrar canal")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
                //if(it.equals("Lima")) channel.close()
                if(it.equals("Lima")){
                    channel.close()
                    return@launch
                }
            }
            //channel.close()
        }
        /*for(value in channel){
            println(value)
        }*/
        while (!channel.isClosedForReceive){
            println(channel.receive())
        }

        //channel.consumeEach { println(it) }
    }
}

fun basicChannel() {
    runBlocking {
        newTopic("Canal basico")
        val channel = Channel<String>()
        launch {
            countries.forEach {
                channel.send(it)
            }
        }

        /*repeat(5){
            println(channel.receive())
        }*/
        for (value in channel){
            println(value)
        }
    }
}
