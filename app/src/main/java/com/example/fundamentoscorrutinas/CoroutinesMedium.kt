package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

fun main(){
    //dispatchers()
   // nested()
    //changewithcontext()
    basicFlows()
}

fun basicFlows() {
    newTopic("Flows basicos")
    runBlocking {
        launch {
            getDataByFlow().collect { println(it)}
        }

        launch {
            (1..50).forEach {
                delay(someTime()/10)
                println("Tarea 2")
            }
        }

    }
}
fun getDataByFlow(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("Procesando datos...")
            delay(someTime())
            emit(20 + it + Random.nextFloat())
        }
    }
}
fun getDataByFlowStatic(): Flow<Float> {
    return flow {
        (1..5).forEach {
            println("Procesando datos...")
            delay(300)
            emit(20 + it + Random.nextFloat())
        }
    }
}


fun changewithcontext() {
    runBlocking {
        newTopic("withContext")
        startMsg()

        withContext(newSingleThreadContext("Pruebadel Curso")){
            startMsg()

            delay(someTime())
            println("CursosAndroid")

            endMsg()
        }
        withContext(Dispatchers.IO){
            startMsg()

            delay(someTime())
            println("Peticion al servidor")

            endMsg()
        }
        endMsg()
    }
}

fun nested() {
    runBlocking {
        newTopic("Anidar")

        val job = launch {
            startMsg()

            launch {
                startMsg()
                delay(someTime())
                println("Otra tarea")
                endMsg()
            }
           val job2 = launch(Dispatchers.IO){
                startMsg()

                launch(newSingleThreadContext("Prueba de Orden")){
                    startMsg()
                    println("Checar la prueba")
                    endMsg()
                }
                delay(someTime())
                println("Tarea del servidor")
                endMsg()
            }
            delay(someTime()/4)
            job2.cancel()
            println("Job de Tarea Cancelado")

            var sum = 0
            (1..100).forEach {
                sum += it
                delay(someTime()/100)
            }
            println("Sum = $sum")
            endMsg()
        }
        delay(someTime()/2)
        job.cancel()
        println("Job cancelado...")
    }
}

fun dispatchers() {
    runBlocking {
        newTopic("Dispatchers")
        launch {
            startMsg()
            println("None")
            endMsg()
        }
        launch(Dispatchers.IO) {
            startMsg()
            println("IO")
            endMsg()
        }
        launch(Dispatchers.Unconfined) {
            startMsg()
            println("Unconfined")
            endMsg()
        }
      /*  launch(Dispatchers.Main) { //Solo funciona con Android en los hilos de la interfaz
            startMsg()
            println("Main")
            endMsg()
        }*/
        launch(Dispatchers.Default) {
            startMsg()
            println("Default")
            endMsg()
        }
        launch(newSingleThreadContext("Primera corrutina")) {
            startMsg()
            println("Mi Corrutina personalizada con un dispatcher")
            endMsg()
        }
        newSingleThreadContext("SegundaCorrutina").use { myContext ->
            launch(myContext) {
                startMsg()
                println("Mi Segunda Corrutina personalizada con un dispatcher")
                endMsg()
            }
        }

    }
}
