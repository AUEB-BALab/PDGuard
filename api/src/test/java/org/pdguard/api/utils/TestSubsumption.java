package org.pdguard.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.pdguard.api.utils.Subsumption;

public class TestSubsumption {

    public enum TetrapodType implements Subsumption {
        MAMMAL,
        BIRD,
        REPTILE,
        AMPHIBIAN;

        public Subsumption getParent() {
            return null;
        }
    }

    enum Tetrapod implements Subsumption {
        WOLF(TetrapodType.MAMMAL),
        LEOPARD(TetrapodType.MAMMAL),
        LION(TetrapodType.MAMMAL),
        SHEEP(TetrapodType.MAMMAL),
        EAGLE(TetrapodType.BIRD),
        COBRA(TetrapodType.REPTILE);

        private Subsumption parent;

        public Subsumption getParent() {
            return parent;
        }

        Tetrapod(Subsumption x) {
            this.parent = x;
        }
    }

    @Test
    public void testSame() {
        Assert.assertTrue(Tetrapod.WOLF.is(Tetrapod.WOLF));
        Assert.assertTrue(TetrapodType.REPTILE.is(TetrapodType.REPTILE));
    }

    @Test
    public void testNotSame() {
        Assert.assertFalse(Tetrapod.WOLF.is(Tetrapod.SHEEP));
        Assert.assertFalse(TetrapodType.REPTILE.is(TetrapodType.MAMMAL));
    }

    @Test
    public void testSuperType() {
        Assert.assertTrue(Tetrapod.WOLF.is(TetrapodType.MAMMAL));
        Assert.assertTrue(Tetrapod.EAGLE.is(TetrapodType.BIRD));
    }

    @Test
    public void testNotSuperType() {
        Assert.assertFalse(TetrapodType.MAMMAL.is(Tetrapod.WOLF));
    }
}
