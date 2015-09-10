import Chisel._

class RegVecVecSample (addrBits : Int) extends Module {
  val io = new Bundle {
    val input  = Valid (UInt (width = 32)).flip;
    val output = Valid (UInt (width = 32));
    val addr0   = Valid (UInt (width = addrBits)).flip;
    val addr1   = Valid (UInt (width = addrBits)).flip;
    val we     = Valid (Bool ()).flip;
  }

  val numOfMem = 1 << addrBits;

  val value = Reg (Vec(Vec(UInt (width = 32),numOfMem),numOfMem));

  io.output.valid := Bool (false);
  io.output.bits := UInt (0);

  when (io.addr0.valid && io.addr1.valid && io.we.valid) {
    when (io.we.bits) {
      // write
      when (io.input.valid) {
        (value (io.addr0.bits)) (io.addr1.bits) := io.input.bits;
      }
    }.otherwise {
      // read
      io.output.bits := (value (io.addr0.bits))(io.addr1.bits);
      io.output.valid := Bool (true);
    }
  }

}

object RegVecVecSample {
  val addrBits = 1;

  def main (args : Array[String]) : Unit = {
    chiselMainTest (args,() => Module (new RegVecVecSample (addrBits))) {
      c => new RegVecVecSampleTests (c)
    }
  }

  class RegVecVecSampleTests (c: RegVecVecSample) extends Tester (c) {
    for (i <- 0 until 10;j <- 0 until 2;k <- 0 until 2) {
      // write
      poke (c.io.input.valid,1);
      poke (c.io.input.bits,i*(j + 1)*(k + 1));
      poke (c.io.addr0.valid,1);
      poke (c.io.addr0.bits,j);
      poke (c.io.addr1.valid,1);
      poke (c.io.addr1.bits,k);

      poke (c.io.we.valid,1);
      poke (c.io.we.bits,1);

      expect (c.io.output.valid,0);
      peek (c.io.output.bits);
      step (1);

      // read
      poke (c.io.input.valid,0);
      poke (c.io.addr0.valid,1);
      poke (c.io.addr0.bits,j);
      poke (c.io.addr1.valid,1);
      poke (c.io.addr1.bits,k);
      poke (c.io.we.valid,1);
      poke (c.io.we.bits,0);

      expect (c.io.output.valid,1);
      expect (c.io.output.bits,i*(j + 1)*(k + 1));
      step (1);
    }
  }
}
