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
package org.spongepowered.api.util.weighted;

import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataSerializable;
import org.spongepowered.api.data.persistence.Queries;
import org.spongepowered.math.GenericMath;

import java.util.Random;
import java.util.StringJoiner;

/**
 * Represents a value which may vary randomly.
 *
 * <p>Implementors of this interface using it in a fashion in which may ever be
 * serialized <strong>must</strong> implement the {@link #toContainer()}
 * method.</p>
 */
@FunctionalInterface
public interface VariableAmount extends DataSerializable {

    /**
     * Creates a new 'fixed' variable amount, calls to {@link #amount} will
     * always return the fixed value.
     *
     * @param value The fixed value
     * @return A variable amount representation
     */
    static VariableAmount fixed(final double value) {
        return new Fixed(value);
    }

    /**
     * Creates a new variable amount which return values between the given min
     * (inclusive) and max (exclusive).
     *
     * @param min The minimum of the range (inclusive)
     * @param max The maximum of the range (exclusive)
     * @return A variable amount representation
     */
    static VariableAmount range(final double min, final double max) {
        return new BaseAndAddition(min, VariableAmount.fixed(max - min));
    }

    /**
     * Creates a new variable about which has a base and variance. The final
     * amount will be the base amount plus or minus a random amount between zero
     * (inclusive) and the variance (exclusive).
     *
     * @param base The base value
     * @param variance The variance
     * @return A variable amount representation
     */
    static VariableAmount baseWithVariance(final double base, final double variance) {
        return new BaseAndVariance(base, VariableAmount.fixed(variance));
    }

    /**
     * Creates a new variable about which has a base and variance. The final
     * amount will be the base amount plus or minus a random amount between zero
     * (inclusive) and the variance (exclusive).
     *
     * @param base The base value
     * @param variance The variance
     * @return A variable amount representation
     */
    static VariableAmount baseWithVariance(final double base, final VariableAmount variance) {
        return new BaseAndVariance(base, variance);
    }

    /**
     * Creates a new variable amount which has a base and an additional amount.
     * The final amount will be the base amount plus a random amount between
     * zero (inclusive) and the additional amount (exclusive).
     *
     * @param base The base value
     * @param addition The additional amount
     * @return A variable amount representation
     */
    static VariableAmount baseWithRandomAddition(final double base, final double addition) {
        return new BaseAndAddition(base, VariableAmount.fixed(addition));
    }

    /**
     * Creates a new variable amount which has a base and an additional amount.
     * The final amount will be the base amount plus a random amount between
     * zero (inclusive) and the additional amount (exclusive).
     *
     * @param base The base value
     * @param addition The additional amount
     * @return A variable amount representation
     */
    static VariableAmount baseWithRandomAddition(final double base, final VariableAmount addition) {
        return new BaseAndAddition(base, addition);
    }

    /**
     * Creates a new variable about which has a base and a chance to apply a
     * random variance. The chance should be between zero and one with a chance
     * of one signifying that the variance will always be applied. If the chance
     * succeeds then the final amount will be the base amount plus or minus a
     * random amount between zero (inclusive) and the variance (exclusive). If
     * the chance fails then the final amount will just be the base value.
     *
     * @param base The base value
     * @param variance The variance
     * @param chance The chance to apply the variance
     * @return A variable amount representation
     */
    static VariableAmount baseWithOptionalVariance(final double base, final double variance, final double chance) {
        return new OptionalAmount(base, chance, VariableAmount.baseWithVariance(base, variance));
    }

    /**
     * Creates a new variable about which has a base and a chance to apply a
     * random variance. The chance should be between zero and one with a chance
     * of one signifying that the variance will always be applied. If the chance
     * succeeds then the final amount will be the base amount plus or minus a
     * random amount between zero (inclusive) and the variance (exclusive). If
     * the chance fails then the final amount will just be the base value.
     *
     * @param base The base value
     * @param variance The variance
     * @param chance The chance to apply the variance
     * @return A variable amount representation
     */
    static VariableAmount baseWithOptionalVariance(final double base, final VariableAmount variance, final double chance) {
        return new OptionalAmount(base, chance, VariableAmount.baseWithVariance(base, variance));
    }

    /**
     * Creates a new variable about which has a base and a chance to apply a
     * random additional amount. The chance should be between zero and one with
     * a chance of one signifying that the additional amount will always be
     * applied. If the chance succeeds then the final amount will be the base
     * amount plus a random amount between zero (inclusive) and the additional
     * amount (exclusive). If the chance fails then the final amount will just
     * be the base value.
     *
     * @param base The base value
     * @param addition The additional amount
     * @param chance The chance to apply the additional amount
     * @return A variable amount representation
     */
    static VariableAmount baseWithOptionalAddition(final double base, final double addition, final double chance) {
        return new OptionalAmount(base, chance, VariableAmount.baseWithRandomAddition(base, addition));
    }

    /**
     * Creates a new variable about which has a base and a chance to apply a
     * random additional amount. The chance should be between zero and one with
     * a chance of one signifying that the additional amount will always be
     * applied. If the chance succeeds then the final amount will be the base
     * amount plus a random amount between zero (inclusive) and the additional
     * amount (exclusive). If the chance fails then the final amount will just
     * be the base value.
     *
     * @param base The base value
     * @param addition The additional amount
     * @param chance The chance to apply the additional amount
     * @return A variable amount representation
     */
    static VariableAmount baseWithOptionalAddition(final double base, final VariableAmount addition, final double chance) {
        return new OptionalAmount(base, chance, VariableAmount.baseWithRandomAddition(base, addition));
    }

    /**
     * Gets an instance of the variable amount depending on the given random
     * object.
     *
     * @param rand The random object
     * @return The amount
     */
    double amount(Random rand);

    /**
     * Gets the amount as if from {@link #amount(Random)} but floored to the
     * nearest integer equivalent.
     *
     * @param rand The random object
     * @return The floored amount
     */
    default int flooredAmount(final Random rand) {
        return GenericMath.floor(this.amount(rand));
    }

    // This is overridden to allow this to be a functional interface as this
    // greatly increases the usability of the interface.
    @Override
    default DataContainer toContainer() {
        throw new UnsupportedOperationException();
    }

    @Override
    default int contentVersion() {
        return 0;
    }

    /**
     * Represents a fixed amount, calls to {@link #amount} will always return
     * the same fixed value.
     */
    class Fixed implements VariableAmount {

        private final double amount;

        Fixed(final double amount) {
            this.amount = amount;
        }

        @Override
        public double amount(final Random rand) {
            return this.amount;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Fixed.class.getSimpleName() + "[", "]")
                .add("amount=" + this.amount)
                .toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Fixed)) {
                return false;
            }
            final Fixed amount = (Fixed) obj;
            return amount.amount == this.amount;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 37 * result + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >> 32));
            return result;
        }

        @Override
        public DataContainer toContainer() {
            return DataContainer.createNew()
                    .set(Queries.CONTENT_VERSION, this.contentVersion())
                    .set(Queries.VARIABLE_AMOUNT, this.amount);
        }

        @Override
        public int contentVersion() {
            return 1;
        }
    }

    /**
     * Represents a base amount with a variance, the final amount will be the
     * base amount plus or minus a random amount between zero (inclusive) and
     * the variance (exclusive).
     */
    class BaseAndVariance implements VariableAmount {

        private final double base;
        private final VariableAmount variance;

        BaseAndVariance(final double base, final VariableAmount variance) {
            this.base = base;
            this.variance = variance;
        }

        @Override
        public double amount(final Random rand) {
            final double var = this.variance.amount(rand);
            return this.base + rand.nextDouble() * var * 2 - var;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", BaseAndVariance.class.getSimpleName() + "[", "]")
                .add("base=" + this.base)
                .add("variance=" + this.variance)
                .toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BaseAndVariance)) {
                return false;
            }
            final BaseAndVariance amount = (BaseAndVariance) obj;
            return amount.base == this.base && amount.variance == this.variance;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 37 * result + (int) (Double.doubleToLongBits(this.base) ^ (Double.doubleToLongBits(this.base) >> 32));
            result = 37 * result + this.variance.hashCode();
            return result;
        }

        @Override
        public DataContainer toContainer() {
            return DataContainer.createNew()
                    .set(Queries.CONTENT_VERSION, this.contentVersion())
                    .set(Queries.VARIABLE_BASE, this.base)
                    .set(Queries.VARIABLE_VARIANCE, this.variance);
        }

        @Override
        public int contentVersion() {
            return 1;
        }

    }

    /**
     * Represents a base amount with a random addition, the final amount will be
     * the base amount plus a random amount between zero (inclusive) and the
     * addition (exclusive).
     */
    class BaseAndAddition implements VariableAmount {

        private final double base;
        private final VariableAmount addition;

        BaseAndAddition(final double base, final VariableAmount addition) {
            this.base = base;
            this.addition = addition;
        }

        @Override
        public double amount(final Random rand) {
            return this.base + (rand.nextDouble() * this.addition.amount(rand));
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", BaseAndAddition.class.getSimpleName() + "[", "]")
                .add("base=" + this.base)
                .add("addition=" + this.addition)
                .toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BaseAndAddition)) {
                return false;
            }
            final BaseAndAddition amount = (BaseAndAddition) obj;
            return amount.base == this.base && amount.addition == this.addition;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 37 * result + (int) (Double.doubleToLongBits(this.base) ^ (Double.doubleToLongBits(this.base) >> 32));
            result = 37 * result + this.addition.hashCode();
            return result;
        }

        @Override
        public DataContainer toContainer() {
            return DataContainer.createNew()
                    .set(Queries.CONTENT_VERSION, this.contentVersion())
                    .set(Queries.VARIABLE_BASE, this.base)
                    .set(Queries.VARIABLE_VARIANCE, this.addition);
        }

        @Override
        public int contentVersion() {
            return 1;
        }
    }

    /**
     * Represents a variable amount which has a base and a chance of varying.
     * This wraps another {@link VariableAmount} which it refers to if the
     * chance succeeds.
     */
    class OptionalAmount implements VariableAmount {

        private final double chance;
        private final double base;
        private final VariableAmount inner;

        OptionalAmount(final double base, final double chance, final VariableAmount inner) {
            this.base = base;
            this.inner = inner;
            this.chance = chance;
        }

        @Override
        public double amount(final Random rand) {
            if (rand.nextDouble() < this.chance) {
                return this.inner.amount(rand);
            }
            return this.base;
        }

        @Override
        public String toString() {
            return new StringJoiner(
                ", ",
                OptionalAmount.class.getSimpleName() + "[",
                "]"
            )
                .add("chance=" + this.chance)
                .add("base=" + this.base)
                .add("inner=" + this.inner)
                .toString();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof OptionalAmount)) {
                return false;
            }
            final OptionalAmount amount = (OptionalAmount) obj;
            return this.inner.equals(amount.inner) && amount.base == this.base && this.chance == amount.chance;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 37 * result + (int) (Double.doubleToLongBits(this.base) ^ (Double.doubleToLongBits(this.base) >> 32));
            result = 37 * result + (int) (Double.doubleToLongBits(this.chance) ^ (Double.doubleToLongBits(this.chance) >> 32));
            result = 37 * result + this.inner.hashCode();
            return result;
        }

        @Override
        public DataContainer toContainer() {
            return DataContainer.createNew()
                    .set(Queries.CONTENT_VERSION, this.contentVersion())
                    .set(Queries.VARIABLE_CHANCE, this.chance)
                    .set(Queries.VARIABLE_BASE, this.base)
                    .set(Queries.VARIABLE_VARIANCE, this.inner);
        }

        @Override
        public int contentVersion() {
            return 1;
        }
    }

}
