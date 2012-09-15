/*
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.facebook.presto.operator;

import com.facebook.presto.TupleInfo;
import com.facebook.presto.block.BlockBuilder;
import com.facebook.presto.block.uncompressed.UncompressedBlock;
import com.facebook.presto.block.uncompressed.UncompressedBlockStream;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.facebook.presto.TupleInfo.Type.VARIABLE_BINARY;
import static com.facebook.presto.block.Blocks.assertBlockStreamEquals;
import static com.facebook.presto.block.Blocks.createBlock;

public class TestMergeOperator
{
    @Test
    public void test()
    {
        List<UncompressedBlock> values = ImmutableList.<UncompressedBlock>builder()
                .add(createBlock(0, "a", "b", "c", "d", "e", "f", "g"))
                .add(createBlock(20, "h", "i", "j", "k", "l", "m"))
                .add(createBlock(30, "n", "o", "p", "q", "r", "s"))
                .build();

        UncompressedBlockStream data = new UncompressedBlockStream(TupleInfo.SINGLE_VARBINARY, values);
        MergeOperator merge = new MergeOperator(data, data, data);

        TupleInfo expectedTupleInfo = new TupleInfo(VARIABLE_BINARY, VARIABLE_BINARY, VARIABLE_BINARY);
        BlockBuilder expectedBlock = new BlockBuilder(0, expectedTupleInfo);
        for (int i = 'a'; i <= 's'; i++) {
            expectedBlock.append(new byte[]{(byte) i});
            expectedBlock.append(new byte[]{(byte) i});
            expectedBlock.append(new byte[]{(byte) i});
        }

        assertBlockStreamEquals(merge, new UncompressedBlockStream(expectedTupleInfo, expectedBlock.build()));
    }
}