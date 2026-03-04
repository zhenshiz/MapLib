package com.map_lib.util.command.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class SimplePathfinder {

    // 最大搜索深度，防止计算卡死服务器
    private static final int MAX_NODES = 2000;

    public static List<BlockPos> findPath(Level level, BlockPos start, BlockPos end) {
        // 简单的 A* 算法实现
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<BlockPos, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, start.distManhattan(end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        int iterations = 0;

        while (!openSet.isEmpty() && iterations < MAX_NODES) {
            iterations++;
            Node current = openSet.poll();

            // 到达终点（允许 1 格误差）
            if (current.pos.distManhattan(end) <= 1) {
                return reconstructPath(current);
            }

            for (BlockPos neighborPos : getNeighbors(current.pos)) {
                // 1. 检查物理可行性 (不走水，不撞头，脚下有地)
                if (!isWalkable(level, neighborPos, current.pos)) {
                    continue;
                }

                double newGCost = current.gCost + 1;
                Node neighborNode = allNodes.getOrDefault(neighborPos, new Node(neighborPos, null, Double.MAX_VALUE, 0));

                if (newGCost < neighborNode.gCost) {
                    neighborNode.parent = current;
                    neighborNode.gCost = newGCost;
                    neighborNode.hCost = neighborPos.distManhattan(end); // 曼哈顿距离作为启发
                    neighborNode.fCost = neighborNode.gCost + neighborNode.hCost;

                    if (!allNodes.containsKey(neighborPos)) {
                        openSet.add(neighborNode);
                        allNodes.put(neighborPos, neighborNode);
                    }
                }
            }
        }

        // 没找到路径或超时
        return Collections.emptyList();
    }

    private static List<BlockPos> reconstructPath(Node node) {
        List<BlockPos> path = new ArrayList<>();
        while (node != null) {
            path.add(node.pos);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    // 获取前后左右上下 6 个邻居
    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        neighbors.add(pos.above()); // 爬梯子或跳跃
        neighbors.add(pos.below()); // 下落
        return neighbors;
    }

    // 核心：判断这个方块能不能走
    private static boolean isWalkable(Level level, BlockPos target, BlockPos from) {
        // 1. 必须是空气或非阻挡方块（如草、花）
        if (isSolid(level, target) || isSolid(level, target.above())) {
            return false; // 头或者脚被堵住了
        }

        // 2. 绝对不能有水/岩浆 (为了安全)
        if (!level.getFluidState(target).isEmpty()) {
            return false;
        }

        // 3. 处理垂直移动
        int yDiff = target.getY() - from.getY();

        if (yDiff > 0) {
            // 向上跳/爬
            if (yDiff > 1) return false; // 不能直接跳 2 格高
            // 如果是爬梯子/藤蔓，允许
            if (isClimbable(level, from) && isClimbable(level, target)) return true;
            // 如果是普通跳跃，需要脚下有方块垫着
            return isSolid(level, from.below());
        } else if (yDiff < 0) {
            // 向下跳/爬
            if (yDiff < -3) return false; // 太高了会摔伤，设定阈值为 3 格
            return true; // 允许自由落体
        }

        // 4. 平面移动：脚下必须有实体方块，或者当前位置是梯子（空中飞人是不行的）
        // 这里有个细节：如果目标位置脚下是空的，那就是跳崖，我们允许跳崖（不超过3格），
        // 但为了稳妥，我们可以要求目标位置脚下必须是实心的，除非在爬梯子。
        boolean targetHasFloor = isSolid(level, target.below());
        boolean isClimbing = isClimbable(level, target);

        return targetHasFloor || isClimbing;
    }

    private static boolean isSolid(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // 这里简单判定：材质完全挡路就是 solid。地毯、栅栏门等特殊情况可以细化。
        return state.isRedstoneConductor(level, pos) || state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS);
    }

    private static boolean isClimbable(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.CLIMBABLE); // 梯子、藤蔓
    }

    private static class Node {
        BlockPos pos;
        Node parent;
        double gCost; // 离起点的距离
        double hCost; // 离终点的估算
        double fCost; // 总权值

        public Node(BlockPos pos, Node parent, double gCost, double hCost) {
            this.pos = pos;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }
    }
}