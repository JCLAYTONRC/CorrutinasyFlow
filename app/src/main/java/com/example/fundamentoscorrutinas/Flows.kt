package com.example.fundamentoscorrutinas

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main(){
    //coldFlow()
    //cancelFlow()
    //flowOperators()
    //terminalFlowOperators()
    //bufferFlow()
    //conflationFlow()
    //multiFlow()
    //flatFlows()
    //flowExceptions()
    completions()
}

fun completions() {
    runBlocking {
        newTopic("Fin de un flujo(onCompletion)")
        getCitiesFlow()
            .onCompletion { println("Quitar el progressbar...") }
            .collect{ println(it)}
        println()

        getMatchResultsFlow()
            .onCompletion { println("Mostrar las estadisticas...") }
            .catch { emit("Error: $this") }
            .collect{ println(it)}

        newTopic("Cancelar Flow")
        getDataByFlowStatic()
            .onCompletion { println("ya no le intera al usuario...") }
            .cancellable()
            .collect{
                if( it > 22.5f) cancel()
                println(it)
            }
    }
}

fun flowExceptions() {
    runBlocking {
        newTopic("Control de errores")
        newTopic("TryCatch")
       /* try {
            getMatchResultsFlow()
                .collect{
                    println(it)
                    if(it.contains("2")) throw  Exception("Habia acorado 1-1 :V")
                }
        }catch (e : Exception){
            e.printStackTrace()
        }*/

        newTopic("Transparencia")
        getMatchResultsFlow()
            .catch {
                emit("Error $this")
            }
            .collect{
                println(it)
                if(!it.contains("-")) println("Notifica al programador...")
            }

    }
}

fun flatFlows() {
    runBlocking {
        newTopic("Flujos de aplanamiento")
        getCitiesFlow()
            .flatMapConcat { city -> // Flow<Flow<TYPE>>
                getDataToFlatFlow(city)
            }
            .map { setFormat(it) }
            .collect{ println(it)}
    }
}

fun getDataToFlatFlow(city: String): Flow<Float> = flow {
    (1..3).forEach {
        println("Temperatura de ayer en $city...")
        emit(Random.nextInt(10,30).toFloat())

        println("Temperatura actual en $city: ")
        delay(100)
        emit(20 + it + Random.nextFloat())
    }
}

fun getCitiesFlow(): Flow<String> = flow {
    listOf("Santander","CDMX","Lima")
        .forEach { city ->
            println("\nConsultando ciudad... ")
            delay(1_000)
            emit(city)
        }
}

fun multiFlow() {
    runBlocking { 
        newTopic("Zip & Combine")
        getDataByFlowStatic()
            .map { setFormat(it) }
            .combine(getMatchResultsFlow()){ degrees, result ->
            //.zip(getMatchResultsFlow()){ degrees, result ->
                "$result with $degrees"
            }
            .collect{ println(it)  }
    }
}

fun conflationFlow() {
    runBlocking {
        newTopic("Fusion")
        val time = measureTimeMillis {
            getMatchResultsFlow()
                .conflate()
                //.collectLatest { //3120ms
                .collect{
                    delay(100)
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun getMatchResultsFlow(): Flow<String> {
    return flow {
        var homeTeam = 0
        var awayTeam = 0
        (0..45).forEach {
            println("minuto $it")
            delay(50)
            homeTeam += Random.nextInt(0,21)/20
            awayTeam += Random.nextInt(0,21)/20
            emit("$homeTeam- $awayTeam")
            if(homeTeam == 2 || awayTeam == 2) throw  Exception("Habia acorado 1 y 1 :V")
        }
    }
}

fun bufferFlow() {
    runBlocking {
        newTopic("Buffer para Flow")
        val time = measureTimeMillis {
            getDataByFlowStatic()
                .map { setFormat(it)}
                .buffer()
                .collect{                   //000111222333444
                    delay(500)  //   0000011111222223333344444
                    println(it)
                }
        }
        println("Time: ${time}ms")
    }
}

fun terminalFlowOperators() {
    runBlocking {
        newTopic("Operadores Flow Terminales")
        newTopic("List")
        val list = getDataByFlow()
           // .toList()
        println("List: $list")

        newTopic("Single")
        val single = getDataByFlow()
            //.take(1)
            //.single()
        println("Single: $single")

        newTopic("First")
        val first = getDataByFlow()
            //.first()
        println("First: $first")

        newTopic("Last")
        val last = getDataByFlow()
            //.last()
        println("Last: $last")

        newTopic("Reduce")
        val saving = getDataByFlow()
            .reduce { accumulator, value ->
                println("Acumulador: $accumulator")
                println("Valor: $value")
                println("Current saving: ${accumulator + value}")
                accumulator + value
            }
        println("Saving: $saving")

        newTopic("Fold")
        val lastSaving = saving
        val totalSaving = getDataByFlow()
            .fold(lastSaving) { acc, value ->
                println("Acumulador: $acc")
                println("Valor: $value")
                println("Current saving: ${acc + value}")
                acc + value
            }
        println("Total Saving: $totalSaving")

    }

}

fun flowOperators() {
    runBlocking {
        newTopic("Operadores Flow Intermediarios")
        newTopic("Map")
        getDataByFlow()
            .map {
                //setFormat(it)
                setFormat(converCelsToFaht(it),"F")
            }
            //.collect{ println(it)}
        newTopic("Filter")
        getDataByFlow()
            .filter {
                it < 23
            }
            .map {
                setFormat(it)
            }

        newTopic("Transform")
        getDataByFlow()
            .transform {
                emit(setFormat(it))
                emit(setFormat(converCelsToFaht(it), "F"))
            }
            //.collect{ println(it)}
        newTopic("Take")
        getDataByFlow()
            .take(3)
            .map { setFormat(it) }
            .collect{ println(it)}
    }
}

fun converCelsToFaht(cels: Float): Float = ((cels *   9) / 5 ) + 32

fun setFormat(temp: Float, degree : String = "C"): String = String.format(Locale.getDefault(),
    "%.1f°$degree",temp)

fun cancelFlow() {
    runBlocking {
        newTopic("Cancelar flow")
        val job = launch {
            getDataByFlow().collect{ println(it)}

        }
        delay(someTime()*2)
        job.cancel()
    }
}

fun coldFlow() {
    newTopic("Flows are Cold")
    runBlocking {
        val dataFlow = getDataByFlow()
        println("Esperando...")
        delay(someTime())
        dataFlow.collect{ println(it)}
    }
}
