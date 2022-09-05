package com.example.fundamentoscorrutinas

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import javax.net.ssl.SSLEngine

fun main(){
    //globalScope()
    //suspendFun()
    newTopic("Constructores de corrutinas")
    //cRunBlocking()
    //cLaunch()
    //cAsync()
    //job()
    //cdeferred()
    cproduce()
    readLine()
}

fun cproduce() {
    newTopic("Produce")
    runBlocking {
        val names = produceName()
        names.consumeEach{ println(it)}
    }


}
fun CoroutineScope.produceName(): ReceiveChannel<String> = produce{
    (1..5).forEach { send("name$it") }
}


fun cdeferred() {
    runBlocking {
        newTopic("Deferred")
        val deferred = async {
            startMsg()
            delay(someTime())
            println("Deferred...")
            endMsg()
            multi(5,2)
        }
        print("Deferred: $deferred")
        print("Valor del Deferred.await: ${deferred.await()}")
    }
}

fun job() {
    runBlocking {
        newTopic("Job")
        val job = launch {
            startMsg()
            delay(2_100)
            println("Job...")
            endMsg()
        }
        println("JOb: $job")
        //delay(4_000)
        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isCompleted: ${job.isCompleted}")

        delay(someTime())
        println("Tarea cancelada o interrumpida")
        job.cancel()
        println("isActive: ${job.isActive}")
        println("isCancelled: ${job.isCancelled}")
        println("isCompleted: ${job.isCompleted}")

    }
}

fun cAsync() {
    runBlocking {
        newTopic("Async")
        async {
            startMsg()
            delay(someTime())
            println("async...")
            endMsg()
        }
    }


}

fun cLaunch() {
    runBlocking {
        newTopic("Launch")
        launch {
            startMsg()
            delay(someTime())
            println("launch...")
            endMsg()
        }
    }
}

fun cRunBlocking() {
    newTopic("RunBlocking")
    runBlocking {
        startMsg()
        delay(someTime())
        println("runBlocking...")
        endMsg()
    }
}

fun suspendFun() {
    newTopic("Suspend")
    Thread.sleep(someTime())
    //delay(someTime())
    GlobalScope.launch { delay(someTime()) }
}

fun globalScope() {
    newTopic("Global Scope")
    GlobalScope.launch {
        startMsg()
        delay(someTime())
        println("Mi corrutina")
        endMsg()
    }
}

fun startMsg() {
    println("Comenzando corrutina -${Thread.currentThread().name}--")
}
fun endMsg() {
    println("Finalizada corrutina -${Thread.currentThread().name}--")
}
