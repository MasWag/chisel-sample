import Chisel._

class RegSample extends Module {
  val io = new Bundle {
    val input  = Valid (UInt (width = 32)).flip;
    val output = Valid (UInt (width = 32));
  }

  val value = Reg (init = (UInt (0,32)));

  when (io.input.valid) {
    value := io.input.bits;
  }

  io.output.valid := (io.input.valid === Bool (false));
  io.output.bits := value;
}

object RegSample {
  def main (args : Array[String]) : Unit = {
    chiselMainTest (args,() => Module (new RegSample ())) {
      c => new RegSampleTests (c)
    }
  }

  class RegSampleTests (c: RegSample) extends Tester (c) {
    for (i <- 0 until 10) {
      poke (c.io.input.valid,1);
      poke (c.io.input.bits,i);
      expect (c.io.output.valid,0);
      peek (c.io.output.bits);
      step (1);
      poke (c.io.input.valid,0);
      expect (c.io.output.valid,1);
      expect (c.io.output.bits,i);
      step (1);
    }
  }
}
