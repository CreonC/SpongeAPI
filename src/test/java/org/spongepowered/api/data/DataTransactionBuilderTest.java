/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.api.data;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.data.DataTransactionResult.Type;

@SuppressWarnings("BadImport") // limited-scope test, it's ok to import common names
class DataTransactionBuilderTest {

    @Test
    void testCorrectTypeWhenAbsorbingResult() {
        Assertions.assertEquals(Type.UNDEFINED, this.absorbedType(Type.UNDEFINED, Type.UNDEFINED));
        Assertions.assertEquals(Type.SUCCESS, this.absorbedType(Type.SUCCESS, Type.UNDEFINED));
        Assertions.assertEquals(Type.FAILURE, this.absorbedType(Type.FAILURE, Type.SUCCESS));
        Assertions.assertEquals(Type.ERROR, this.absorbedType(Type.FAILURE, Type.ERROR));
        Assertions.assertEquals(Type.CANCELLED, this.absorbedType(Type.FAILURE, Type.CANCELLED));
    }
    
    private Type absorbedType(final Type builderType, final Type resultType) {
        final DataTransactionResult result = DataTransactionResult.builder().result(resultType).build();
        final DataTransactionResult absorbed = DataTransactionResult.builder().result(builderType).absorbResult(result).build();
        return absorbed.type();
    }

}
