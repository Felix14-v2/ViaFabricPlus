/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowableFluid.class)
public abstract class MixinFlowableFluid {

    @Redirect(method = "isFlowBlocked", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSideSolidFullSquare(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z"))
    private boolean modifyIsSolidBlock(BlockState instance, BlockView blockView, BlockPos blockPos, Direction direction) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
            final ViaFabricPlusMappingDataLoader.Material material = ViaFabricPlusMappingDataLoader.MATERIALS.get(ViaFabricPlusMappingDataLoader.getBlockMaterial(instance.getBlock()));
            return material.solid();
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            final Block block = instance.getBlock();
            if (block instanceof ShulkerBoxBlock || block instanceof LeavesBlock || block instanceof TrapdoorBlock ||
                    block == Blocks.BEACON || block == Blocks.CAULDRON || block == Blocks.GLASS ||
                    block == Blocks.GLOWSTONE || block == Blocks.ICE || block == Blocks.SEA_LANTERN ||
                    block instanceof StainedGlassBlock || block == Blocks.PISTON || block == Blocks.STICKY_PISTON ||
                    block == Blocks.PISTON_HEAD || block instanceof StairsBlock) {
                return false;
            }
        }
        return instance.isSideSolidFullSquare(blockView, blockPos, direction);
    }

}
