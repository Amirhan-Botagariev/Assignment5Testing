/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.poifs.filesystem;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.poi.POIDataSamples;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Mini Store in the NIO POIFS
 */
@SuppressWarnings("CatchMayIgnoreException")
final class TestPOIFSMiniStore {
    private static final POIDataSamples _inst = POIDataSamples.getPOIFSInstance();

    /**
     * Check that for a given mini block, we can correctly figure
     * out what the next one is
     */
    @Test
    void testNextBlock() throws Exception {
        // It's the same on 512 byte and 4096 byte block files!
        POIFSFileSystem fsA = new POIFSFileSystem(_inst.getFile("BlockSize512.zvi"));
        POIFSFileSystem fsB = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize512.zvi"));
        POIFSFileSystem fsC = new POIFSFileSystem(_inst.getFile("BlockSize4096.zvi"));
        POIFSFileSystem fsD = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize4096.zvi"));
        for (POIFSFileSystem fs : new POIFSFileSystem[]{fsA, fsB, fsC, fsD}) {
            POIFSMiniStore ministore = fs.getMiniStore();

            // 0 -> 51 is one stream
            for (int i = 0; i < 50; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(50));

            // 51 -> 103 is the next
            for (int i = 51; i < 103; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(103));

            // Then there are 3 one block ones
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(104));
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(105));
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(106));

            // 107 -> 154 is the next
            for (int i = 107; i < 154; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(154));

            // 155 -> 160 is the next
            for (int i = 155; i < 160; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(160));

            // 161 -> 166 is the next
            for (int i = 161; i < 166; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(166));

            // 167 -> 172 is the next
            for (int i = 167; i < 172; i++) {
                assertEquals(i + 1, ministore.getNextBlock(i));
            }
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(172));

            // Now some short ones
            assertEquals(174, ministore.getNextBlock(173));
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(174));

            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(175));

            assertEquals(177, ministore.getNextBlock(176));
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(177));

            assertEquals(179, ministore.getNextBlock(178));
            assertEquals(180, ministore.getNextBlock(179));
            assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(180));

            // 181 onwards is free
            for (int i = 181; i < fs.getBigBlockSizeDetails().getBATEntriesPerBlock(); i++) {
                assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(i));
            }
        }
        fsD.close();
        fsC.close();
        fsB.close();
        fsA.close();
    }

    /**
     * Check we get the right data back for each block
     */
    @Test
    void testGetBlock() throws Exception {
        // It's the same on 512 byte and 4096 byte block files!
        POIFSFileSystem fsA = new POIFSFileSystem(_inst.getFile("BlockSize512.zvi"));
        POIFSFileSystem fsB = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize512.zvi"));
        POIFSFileSystem fsC = new POIFSFileSystem(_inst.getFile("BlockSize4096.zvi"));
        POIFSFileSystem fsD = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize4096.zvi"));
        for (POIFSFileSystem fs : new POIFSFileSystem[]{fsA, fsB, fsC, fsD}) {
            // Mini stream should be at big block zero
            assertEquals(0, fs._get_property_table().getRoot().getStartBlock());

            // Grab the ministore
            POIFSMiniStore ministore = fs.getMiniStore();
            ByteBuffer b;

            // Runs from the start of the data section in 64 byte chungs
            b = ministore.getBlockAt(0);
            assertEquals((byte) 0x9e, b.get());
            assertEquals((byte) 0x75, b.get());
            assertEquals((byte) 0x97, b.get());
            assertEquals((byte) 0xf6, b.get());
            assertEquals((byte) 0xff, b.get());
            assertEquals((byte) 0x21, b.get());
            assertEquals((byte) 0xd2, b.get());
            assertEquals((byte) 0x11, b.get());

            // And the next block
            b = ministore.getBlockAt(1);
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x03, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x12, b.get());
            assertEquals((byte) 0x02, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());

            // Check the last data block
            b = ministore.getBlockAt(180);
            assertEquals((byte) 0x30, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x00, b.get());
            assertEquals((byte) 0x80, b.get());

            // And the rest until the end of the big block is zeros
            for (int i = 181; i < 184; i++) {
                b = ministore.getBlockAt(i);
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
                assertEquals((byte) 0, b.get());
            }
        }
        fsD.close();
        fsC.close();
        fsB.close();
        fsA.close();
    }

    /**
     * Ask for free blocks where there are some already
     * to be had from the SFAT
     */
    @Test
    void testGetFreeBlockWithSpare() throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(_inst.getFile("BlockSize512.zvi"));
        POIFSMiniStore ministore = fs.getMiniStore();

        // Our 2nd SBAT block has spares
        assertFalse(ministore.getBATBlockAndIndex(0).getBlock().hasFreeSectors());
        assertTrue(ministore.getBATBlockAndIndex(128).getBlock().hasFreeSectors());

        // First free one at 181
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(181));
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(182));
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(183));
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(184));

        // Ask, will get 181
        assertEquals(181, ministore.getFreeBlock());

        // Ask again, will still get 181 as not written to
        assertEquals(181, ministore.getFreeBlock());

        // Allocate it, then ask again
        ministore.setNextBlock(181, POIFSConstants.END_OF_CHAIN);
        assertEquals(182, ministore.getFreeBlock());

        fs.close();
    }

    /**
     * Ask for free blocks where no free ones exist, and so the
     * stream needs to be extended and another SBAT added
     */
    @Test
    void testGetFreeBlockWithNoneSpare() throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize512.zvi"));
        POIFSMiniStore ministore = fs.getMiniStore();

        // We've spare ones from 181 to 255
        for (int i = 181; i < 256; i++) {
            assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(i));
        }

        // Check our SBAT free stuff is correct
        assertFalse(ministore.getBATBlockAndIndex(0).getBlock().hasFreeSectors());
        assertTrue(ministore.getBATBlockAndIndex(128).getBlock().hasFreeSectors());

        // Allocate all the spare ones
        for (int i = 181; i < 256; i++) {
            ministore.setNextBlock(i, POIFSConstants.END_OF_CHAIN);
        }

        // SBAT are now full, but there's only the two
        assertFalse(ministore.getBATBlockAndIndex(0).getBlock().hasFreeSectors());
        assertFalse(ministore.getBATBlockAndIndex(128).getBlock().hasFreeSectors());
        assertThrows(IndexOutOfBoundsException.class, () -> ministore.getBATBlockAndIndex(256), "Should only be two SBATs");

        // Now ask for a free one, will need to extend the SBAT chain
        assertEquals(256, ministore.getFreeBlock());

        assertFalse(ministore.getBATBlockAndIndex(0).getBlock().hasFreeSectors());
        assertFalse(ministore.getBATBlockAndIndex(128).getBlock().hasFreeSectors());
        assertTrue(ministore.getBATBlockAndIndex(256).getBlock().hasFreeSectors());
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(254)); // 2nd SBAT
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(255)); // 2nd SBAT
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(256)); // 3rd SBAT
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(257)); // 3rd SBAT

        fs.close();
    }

    /**
     * Test that we will extend the underlying chain of
     * big blocks that make up the ministream as needed
     */
    @Test
    void testCreateBlockIfNeeded() throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(_inst.openResourceAsStream("BlockSize512.zvi"));
        POIFSMiniStore ministore = fs.getMiniStore();

        // 178 -> 179 -> 180, 181+ is free
        assertEquals(179, ministore.getNextBlock(178));
        assertEquals(180, ministore.getNextBlock(179));
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(180));
        for (int i = 181; i < 256; i++) {
            assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(i));
        }

        // However, the ministore data only covers blocks to 183
        for (int i = 0; i <= 183; i++) {
            ministore.getBlockAt(i);
        }
        assertThrows(NoSuchElementException.class, () -> ministore.getBlockAt(184), "No block at 184");

        // The ministore itself is made up of 23 big blocks
        Iterator<ByteBuffer> it = new POIFSStream(fs, fs.getRoot().getProperty().getStartBlock()).getBlockIterator();
        int count = 0;
        while (it.hasNext()) {
            count++;
            it.next();
        }
        assertEquals(23, count);

        // Ask it to get block 184 with creating, it will do
        ministore.createBlockIfNeeded(184);

        // The ministore should be one big block bigger now
        it = new POIFSStream(fs, fs.getRoot().getProperty().getStartBlock()).getBlockIterator();
        count = 0;
        while (it.hasNext()) {
            count++;
            it.next();
        }
        assertEquals(24, count);

        // The mini block block counts now run to 191
        for (int i = 0; i <= 191; i++) {
            ministore.getBlockAt(i);
        }

        assertThrows(NoSuchElementException.class, () -> ministore.getBlockAt(192), "No block at 192");

        // Now try writing through to 192, check that the SBAT and blocks are there
        byte[] data = new byte[15 * 64];
        POIFSStream stream = new POIFSStream(ministore, 178);
        stream.updateContents(data);

        // Check now
        assertEquals(179, ministore.getNextBlock(178));
        assertEquals(180, ministore.getNextBlock(179));
        assertEquals(181, ministore.getNextBlock(180));
        assertEquals(182, ministore.getNextBlock(181));
        assertEquals(183, ministore.getNextBlock(182));
        assertEquals(184, ministore.getNextBlock(183));
        assertEquals(185, ministore.getNextBlock(184));
        assertEquals(186, ministore.getNextBlock(185));
        assertEquals(187, ministore.getNextBlock(186));
        assertEquals(188, ministore.getNextBlock(187));
        assertEquals(189, ministore.getNextBlock(188));
        assertEquals(190, ministore.getNextBlock(189));
        assertEquals(191, ministore.getNextBlock(190));
        assertEquals(192, ministore.getNextBlock(191));
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(192));
        for (int i = 193; i < 256; i++) {
            assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(i));
        }

        fs.close();
    }

    @Test
    void testCreateMiniStoreFirst() throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem();
        POIFSMiniStore ministore = fs.getMiniStore();
        DocumentInputStream dis;
        DocumentEntry entry;

        // Initially has Properties + BAT but nothing else
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(2));
        // Ministore has no blocks, so can't iterate until used
        try {
            ministore.getNextBlock(0);
        } catch (IndexOutOfBoundsException e) {
        }

        // Write a very small new document, will populate the ministore for us
        byte[] data = new byte[8];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i + 42);
        }
        fs.getRoot().createDocument("mini", new ByteArrayInputStream(data));

        // Should now have a mini-fat and a mini-stream
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(2));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(3));
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(4));
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(0));
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(1));

        // Re-fetch the mini store, and add it a second time
        ministore = fs.getMiniStore();
        fs.getRoot().createDocument("mini2", new ByteArrayInputStream(data));

        // Main unchanged, ministore has a second
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(2));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(3));
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(4));
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(0));
        assertEquals(POIFSConstants.END_OF_CHAIN, ministore.getNextBlock(1));
        assertEquals(POIFSConstants.UNUSED_BLOCK, ministore.getNextBlock(2));

        // Check the data is unchanged and the right length
        entry = (DocumentEntry) fs.getRoot().getEntryCaseInsensitive("mini");
        assertEquals(data.length, entry.getSize());
        byte[] rdata = new byte[data.length];
        dis = new DocumentInputStream(entry);
        IOUtils.readFully(dis, rdata);
        assertArrayEquals(data, rdata);
        dis.close();

        entry = (DocumentEntry) fs.getRoot().getEntryCaseInsensitive("mini2");
        assertEquals(data.length, entry.getSize());
        rdata = new byte[data.length];
        dis = new DocumentInputStream(entry);
        IOUtils.readFully(dis, rdata);
        assertArrayEquals(data, rdata);
        dis.close();

        // Done
        fs.close();
    }

    @Test
    void testMultiBlockStream() throws Exception {
        byte[] data1B = new byte[63];
        byte[] data2B = new byte[64 + 14];
        for (int i = 0; i < data1B.length; i++) {
            data1B[i] = (byte) (i + 2);
        }
        for (int i = 0; i < data2B.length; i++) {
            data2B[i] = (byte) (i + 4);
        }

        // New filesystem and store to use
        POIFSFileSystem fs = new POIFSFileSystem();

        // Initially has Properties + BAT but nothing else
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(2));

        // Store the 2 block one, should use 2 mini blocks, and request
        // the use of 2 big blocks
        POIFSMiniStore ministore = fs.getMiniStore();
        fs.getRoot().createDocument("mini2", new ByteArrayInputStream(data2B));

        // Check
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(2)); // SBAT
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(3)); // Mini
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(4));

        // First 2 Mini blocks will be used
        assertEquals(2, ministore.getFreeBlock());

        // Add one more mini-stream, and check
        fs.getRoot().createDocument("mini1", new ByteArrayInputStream(data1B));

        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(0));
        assertEquals(POIFSConstants.FAT_SECTOR_BLOCK, fs.getNextBlock(1));
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(2)); // SBAT
        assertEquals(POIFSConstants.END_OF_CHAIN, fs.getNextBlock(3)); // Mini
        assertEquals(POIFSConstants.UNUSED_BLOCK, fs.getNextBlock(4));

        // One more mini-block will be used
        assertEquals(3, ministore.getFreeBlock());

        // Check the contents too
        byte[] r1 = new byte[data1B.length];
        DocumentInputStream dis = fs.createDocumentInputStream("mini1");
        IOUtils.readFully(dis, r1);
        dis.close();
        assertArrayEquals(data1B, r1);

        byte[] r2 = new byte[data2B.length];
        dis = fs.createDocumentInputStream("mini2");
        IOUtils.readFully(dis, r2);
        dis.close();
        assertArrayEquals(data2B, r2);
        fs.close();
    }

    /**
     * Check the computation of the mini stream size when the mini FAT sectors contain unallocated mini sectors.
     * https://github.com/apache/poi/pull/182
     */
    @Test
    void testComputeSize() throws Exception {
        try (POIFSFileSystem fs = new POIFSFileSystem()) {
            fs.getPropertyTable().getRoot().setStorageClsid(new ClassID("000C108400000000C000000000000046")); // MSI storage class

            // create 8 mini FAT sectors fully allocated
            for (int i = 0; i < 8 * 128; i++) {
                fs.getRoot().createDocument("Entry " + i, new ByteArrayInputStream(new byte[64]));
            }

            // Mini FAT Sector #1: Unallocate all the mini sectors
            for (int i = 0; i < 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #2: Unallocate 8 mini sectors at the beginning
            for (int i = 128; i < 128 + 8; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #3: Unallocate 4 mini sectors in the middle
            for (int i = 2 * 128 + 64; i < 2 * 128 + 64 + 4; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #4: Unallocate all the mini sectors
            for (int i = 3 * 128; i < 4 * 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #5: Unallocate 32 mini sectors at the end
            for (int i = 5 * 128 - 32; i < 5 * 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #6: Unallocate 64 mini sectors at the beginning and 16 mini sectors at the end
            for (int i = 5 * 128; i < 5 * 128 + 64; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }
            for (int i = 6 * 128 - 16; i < 6 * 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #7: Unallocate all the mini sectors
            for (int i = 6 * 128; i < 7 * 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            // Mini FAT Sector #8: Unallocate all the mini sectors
            for (int i = 7 * 128; i < 8 * 128; i++) {
                fs.getRoot().getEntry("Entry " + i).delete();
            }

            fs.writeFilesystem(NullOutputStream.INSTANCE);

            assertEquals(48128, fs.getPropertyTable().getRoot().getSize(), "mini stream size");
        }
    }
}
