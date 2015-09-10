import Chisel._

class RegVecSample (addrBits : Int) extends Module {
  val io = new Bundle {
    val input  = Valid (UInt (width = 32)).flip;
    val output = Valid (UInt (width = 32));
    val addr   = Valid (UInt (width = addrBits)).flip;
    val we     = Valid (Bool ()).flip;
  }

  val numOfMem = 1 << addrBits;

  val value = Reg (Vec(UInt (width = 32),numOfMem));

  io.output.valid := Bool (false);
  io.output.bits := UInt (0);

  when (io.addr.valid && io.we.valid) {
    when (io.we.bits) {
      // write
      when (io.input.valid) {
        value (io.addr.bits) := io.input.bits;
      }
    }.otherwise {
      // read
      io.output.bits := value (io.addr.bits);
      io.output.valid := Bool (true);
    }
  }

}

object RegVecSample {
  val addrBits = 1;

  def main (args : Array[String]) : Unit = {
    chiselMainTest (args,() => Module (new RegVecSample (addrBits))) {
      c => new RegVecSampleTests (c)
    }
  }

  class RegVecSampleTests (c: RegVecSample) extends Tester (c) {
    for (i <- 0 until 10;j <- 0 until 2) {
      // write
      poke (c.io.input.valid,1);
      poke (c.io.input.bits,i*(j + 1));
      poke (c.io.addr.valid,1);
      poke (c.io.addr.bits,j);
      poke (c.io.we.valid,1);
      poke (c.io.we.bits,1);

      expect (c.io.output.valid,0);
      peek (c.io.output.bits);
      step (1);

      // read
      poke (c.io.input.valid,0);
      poke (c.io.addr.valid,1);
      poke (c.io.addr.bits,j);
      poke (c.io.we.valid,1);
      poke (c.io.we.bits,0);

      expect (c.io.output.valid,1);
      expect (c.io.output.bits,i*(j + 1));
      step (1);
    }
  }
}
