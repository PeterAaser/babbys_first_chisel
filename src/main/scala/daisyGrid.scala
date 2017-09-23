package Core
import Chisel._

class daisyGrid(rows: Int, cols: Int, dataWidth: Int) extends Module{

  val io = new Bundle {

    val readEnable = Bool(INPUT)
    val dataIn     = UInt(INPUT, dataWidth)
    val readRow    = UInt(INPUT, 8)
    val reset      = Bool(INPUT)

    val dataOut    = UInt(OUTPUT, dataWidth)
  }

  val currentRowIndex = Reg(init=(UInt(0, 8)))
  val currentColIndex = Reg(init=(UInt(0, 8)))

  val memRows = Vec.fill(rows){ Module(new daisyVector(cols, dataWidth)).io }
  val elements = rows*cols


  io.dataOut := UInt(0)

  for(ii <- 0 until rows){

    memRows(ii).readEnable := UInt(0)
    memRows(ii).dataIn := io.dataIn

    when(io.readRow === UInt(ii)){
      memRows(ii).readEnable := io.readEnable
      io.dataOut := memRows(ii).dataOut
    }
  }
}

class daisyGridTest(c: daisyGrid) extends Tester(c) {

  poke(c.io.readEnable, 1)
  for(ii <- 0 until 12){
    poke(c.io.dataIn, ii)
    poke(c.io.readRow, ii/3)
    step(1)
    println("////////////////////")
  }
  poke(c.io.readEnable, 0)
  for(ii <- 0 until 12){
    peek(c.io.dataOut)
    poke(c.io.readRow, ii/3)
    step(1)
    println("////////////////////")
  }
}
