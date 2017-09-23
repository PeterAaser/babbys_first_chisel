package Core
import Chisel._


object CoreMain {
  def main(args: Array[String]): Unit = {
    // chiselMainTest(args, () => Module(new daisyVector(4, 32))) { c => new daisyVectorTest(c) }
    // chiselMainTest(args, () => Module(new daisyGrid(4, 3, 32))) { c => new daisyGridTest(c) }
    chiselMainTest(args, () => Module(new daisyMultiplier(3, 2, 2, 3, 32))) { c => new daisyMultiplierTest(c) }
  }
}

class Tile(data_width: Int, cols: Int, rows: Int) extends Module{

  val io = new Bundle {
    val data_in = UInt(INPUT, data_width)
    val reset = Bool(INPUT)

    val data_out = UInt(OUTPUT, data_width)
    val data_out_delayed = UInt(OUTPUT, data_width)
  }

  val data_reg = Reg(init=UInt(0, width = data_width))

  io.data_out := io.data_in
  data_reg := io.data_in
  io.data_out_delayed := data_reg
}

class myTest(c: Tile) extends Tester(c) {

  poke(c.io.data_in, 0)
  peek(c.io.data_out_delayed)
  step(1)
  poke(c.io.data_in, 1)
  peek(c.io.data_out)
  peek(c.io.data_out_delayed)
  step(1)
  poke(c.io.data_in, 2)
  peek(c.io.data_out)
  peek(c.io.data_out_delayed)
  step(1)
  poke(c.io.data_in, 3)
  peek(c.io.data_out)
  peek(c.io.data_out_delayed)

}

object Util {
  def somefun(someval: Int) : Unit = {}
}
