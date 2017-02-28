/*
 * Copyright © 2016 - 2017 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the “License”); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an “AS IS” BASIS, without warranties or conditions of any kind,
 * EITHER EXPRESS OR IMPLIED. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.vrg.rapid;

import com.google.common.net.HostAndPort;
import com.vrg.rapid.pb.LinkStatus;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for a watermark-buffer
 */
public class WatermarkBufferTest {
    private static final int K = 10;
    private static final int H = 8;
    private static final int L = 3;
    private static final long configurationId = -1;  // Should not affect the following tests

    /**
     * A series of updates with the right ring indexes
     */
    @Test
    public void waterMarkTest() {
        final WatermarkBuffer wb = new WatermarkBuffer(K, H, L);
        final HostAndPort dst = HostAndPort.fromParts("127.0.0.2", 2);
        List<WatermarkBuffer.Node> ret;

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst, LinkStatus.UP, configurationId), H - 1);
        assertEquals(1, ret.size());
        assertEquals(1, wb.getNumProposals());
    }

    @Test
    public void waterMarkTestBlockingOneBlocker() {
        final WatermarkBuffer wb = new WatermarkBuffer(K, H, L);
        final HostAndPort dst1 = HostAndPort.fromParts("127.0.0.2", 2);
        final HostAndPort dst2 = HostAndPort.fromParts("127.0.0.3", 2);
        List<WatermarkBuffer.Node> ret;

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst1, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst2, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst1, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst2, LinkStatus.UP, configurationId), H - 1);
        assertEquals(2, ret.size());
        assertEquals(1, wb.getNumProposals());
    }


    @Test
    public void waterMarkTestBlockingThreeBlockers() {
        final WatermarkBuffer wb = new WatermarkBuffer(K, H, L);
        final HostAndPort dst1 = HostAndPort.fromParts("127.0.0.2", 2);
        final HostAndPort dst2 = HostAndPort.fromParts("127.0.0.3", 2);
        final HostAndPort dst3 = HostAndPort.fromParts("127.0.0.4", 2);
        List<WatermarkBuffer.Node> ret;

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst1, LinkStatus.UP, configurationId),i );
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst2, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst3, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst1, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst3, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst2, LinkStatus.UP, configurationId), H - 1);
        assertEquals(3, ret.size());
        assertEquals(1, wb.getNumProposals());
    }

    @Test
    public void waterMarkTestBlockingMultipleBlockersPastH() {
        final WatermarkBuffer wb = new WatermarkBuffer(K, H, L);
        final HostAndPort dst1 = HostAndPort.fromParts("127.0.0.2", 2);
        final HostAndPort dst2 = HostAndPort.fromParts("127.0.0.3", 2);
        final HostAndPort dst3 = HostAndPort.fromParts("127.0.0.4", 2);
        List<WatermarkBuffer.Node> ret;

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst1, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst2, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst3, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        // Unlike the previous test, add more reports for
        // dst1 and dst3 past the H boundary.
        wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst1, LinkStatus.UP, configurationId), H - 1);
        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H + 1), dst1, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());

        wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst3, LinkStatus.UP, configurationId), H - 1);
        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H + 1), dst3, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());


        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst2, LinkStatus.UP, configurationId), H - 1);
        assertEquals(3, ret.size());
        assertEquals(1, wb.getNumProposals());
    }

    @Test
    public void waterMarkTestBelowL() {
        final WatermarkBuffer wb = new WatermarkBuffer(K, H, L);
        final HostAndPort dst1 = HostAndPort.fromParts("127.0.0.2", 2);
        final HostAndPort dst2 = HostAndPort.fromParts("127.0.0.3", 2);
        final HostAndPort dst3 = HostAndPort.fromParts("127.0.0.4", 2);
        List<WatermarkBuffer.Node> ret;

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst1, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        // Unlike the previous test, dst2 has < L updates
        for (int i = 0; i < L - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst2, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        for (int i = 0; i < H - 1; i++) {
            ret = wb.aggregateForProposal(new LinkUpdateMessage(
                    HostAndPort.fromParts("127.0.0.1", i + 1), dst3, LinkStatus.UP, configurationId), i);
            assertEquals(0, ret.size());
            assertEquals(0, wb.getNumProposals());
        }

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst1, LinkStatus.UP, configurationId), H - 1);
        assertEquals(0, ret.size());
        assertEquals(0, wb.getNumProposals());

        ret = wb.aggregateForProposal(new LinkUpdateMessage(
                HostAndPort.fromParts("127.0.0.1", H), dst3, LinkStatus.UP, configurationId), H - 1);
        assertEquals(2, ret.size());
        assertEquals(1, wb.getNumProposals());
    }
}
