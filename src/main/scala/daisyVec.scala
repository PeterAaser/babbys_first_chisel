package Core
import Chisel._

class daisyVector(elements: Int, dataWidth: Int) extends Module{

  val io = new Bundle {
    val readEnable = Bool(INPUT)
    val dataIn     = UInt(INPUT, dataWidth)
    val reset      = Bool(INPUT)

    val dataOut    = UInt(OUTPUT, dataWidth)
  }

  val currentIndex = Reg(init=(UInt(0, 8)))
  val memory = Vec.fill(elements){ Reg(UInt(width = dataWidth)) }

  when(currentIndex === UInt(elements - 1)){
    currentIndex := UInt(0)
  }.otherwise{
    currentIndex := currentIndex + UInt(1)
  }


  io.dataOut := UInt(0)

  for(ii <- 0 until elements){
    when(currentIndex === UInt(ii)){
      when(io.readEnable === Bool(true)){
        memory(ii) := io.dataIn
      }
      io.dataOut := memory(ii)
    }
  }
}

class daisyVectorTest(c: daisyVector) extends Tester(c) {

  poke(c.io.readEnable, 1)
  step(1)

  for(ii <- 0 until 4){
    poke(c.io.dataIn, ii)
    println("////////////////////")
    step(1)
  }

  poke(c.io.readEnable, 0)
  for(ii <- 0 until 4){
    peek(c.io.dataOut)
    step(1)
  }
}
