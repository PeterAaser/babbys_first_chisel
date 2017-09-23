package Core
import Chisel._
import scala.language.reflectiveCalls

// I use val for the args so I can reference them in the test
class daisyMultiplier(val rowsA: Int, val colsA: Int, val rowsB: Int, val colsB: Int, val dataWidth: Int) extends Module {

  val io = new Bundle {

    val dataInA     = UInt(INPUT, dataWidth)
    val readEnableA = Bool(INPUT)

    val dataInB     = UInt(INPUT, dataWidth)
    val readEnableB = Bool(INPUT)

    val dataOut   = UInt(OUTPUT, dataWidth)
    val dataValid = Bool(OUTPUT)
    val done      = Bool(OUTPUT)
  }

  // How many cycles does it take to fill the matrices with data?
  val rowCounter       = Reg(init =UInt(0, 8))
  val colCounter       = Reg(init =UInt(0, 8))

  val rowOutputCounter = Reg(init =UInt(0, 8))

  val calculating      = Reg(init =Bool(false))
  val accumulator      = Reg(init =UInt(0, 8))

  val resultReady      = Reg(init = Bool(false))

  println(s"rowsA: $rowsA, colsA: $colsA, rowsB: $rowsB, colsB: $colsB")

  ////////////////////////////////////////
  ////////////////////////////////////////
  /// We transpose matrix B. This means that if both matrices read the same input
  /// stream then they will end up transposed.
  val matrixA = Module(new daisyGrid(rowsA, colsA, dataWidth)).io
  val matrixB = Module(new daisyGrid(colsB, rowsB, dataWidth)).io

  matrixA.dataIn := io.dataInA
  matrixA.readEnable := io.readEnableA

  matrixB.dataIn := io.dataInB
  matrixB.readEnable := io.readEnableB



  ////////////////////////////////////////
  ////////////////////////////////////////
  /// Set up counter statemachine
  io.done := Bool(false)

  when(colCounter === UInt(colsA - 1)){
    colCounter := UInt(0)

    when(rowCounter === UInt(rowsA - 1)){
      rowCounter := UInt(0)
      calculating := Bool(true)

      when(calculating === Bool(true)){

        when(rowOutputCounter === UInt(rowsA - 1)){
          io.done := Bool(true)
        }.otherwise{
          rowOutputCounter := rowOutputCounter + UInt(1)
        }

      }

    }.otherwise{
      rowCounter := rowCounter + UInt(1)
    }
  }.otherwise{
    colCounter := colCounter + UInt(1)
  }



  ////////////////////////////////////////
  ////////////////////////////////////////
  /// set up reading patterns depending on if we are in calculating state or not
  when(calculating === Bool(true)){
    matrixA.readRow := rowOutputCounter
  }.otherwise{
    matrixA.readRow := rowCounter
  }

  matrixB.readRow := rowCounter



  ////////////////////////////////////////
  ////////////////////////////////////////
  /// when we're in calculating mode, check if we have valid output
  resultReady := Bool(false)
  io.dataValid := Bool(false)
  when(calculating === Bool(true)){
    when(colCounter === UInt(colsA - 1)){
      resultReady := Bool(true)
    }
  }


  ////////////////////////////////////////
  ////////////////////////////////////////
  /// when we've got a result ready we need to flush the accumulator
  when(resultReady === Bool(true)){
    // To flush our accumulator we simply disregard previous state
    accumulator := (matrixA.dataOut*matrixB.dataOut)
    io.dataValid := Bool(true)
  }.otherwise{
    accumulator := accumulator + (matrixA.dataOut*matrixB.dataOut)
  }
  io.dataOut := accumulator
}


class daisyMultiplierTest(c: daisyMultiplier) extends Tester(c) {

  poke(c.io.readEnableA, 1)
  poke(c.io.readEnableB, 1)
  for(ii <- 0 until 6){
    println("data in:")
    poke(c.io.dataInA, (ii/2) + 1)
    poke(c.io.dataInB, (ii/2) + 1)
    println("fill counters")
    peek(c.colCounter)
    peek(c.rowCounter)
    peek(c.rowOutputCounter)
    println("ready?")
    peek(c.calculating)
    println("data from matrices")
    peek(c.matrixA.dataOut)
    peek(c.matrixB.dataOut)
    step(1)
    println("////////////////////\n")
  }

  // poke(c.io.readEnableA, 0)
  // poke(c.io.readEnableB, 0)

  // println("\n\n////////////////////")
  // println("////////////////////")
  // println("data filled")
  // println("////////////////////")
  // println("////////////////////\n\n")

  // for(ii <- 0 until 19){
  //   println("data out")
  //   peek(c.io.dataOut)
  //   peek(c.io.dataValid)
  //   peek(c.accumulator)
  //   step(1)
  //   println("////////////////////\n")
  // }
}
