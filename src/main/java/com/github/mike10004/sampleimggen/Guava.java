package com.github.mike10004.sampleimggen;

/**
 * Static utility methods copied from Guava. License statements are included above
 * each class definition.
 */
class Guava {

    private Guava() {}

    /*
     * Copyright (C) 2011 The Guava Authors
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
     * in compliance with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software distributed under the License
     * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
     * or implied. See the License for the specific language governing permissions and limitations under
     * the License.
     */
    public static class IntMath {

        private IntMath() {}

        /**
         * Returns the product of {@code a} and {@code b}, provided it does not overflow.
         *
         * @throws ArithmeticException if {@code a * b} overflows in signed {@code int} arithmetic
         */
        public static int checkedMultiply(int a, int b) {
            long result = (long) a * b;
            checkNoOverflow(result == (int) result);
            return (int) result;
        }
    }
    static void checkNoOverflow(boolean condition) {
        if (!condition) {
            throw new ArithmeticException("overflow");
        }
    }

    /*
     * Copyright (C) 2011 The Guava Authors
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
     * in compliance with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software distributed under the License
     * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
     * or implied. See the License for the specific language governing permissions and limitations under
     * the License.
     */
    public static class LongMath {

        private LongMath() {}

        /**
         * Returns the product of {@code a} and {@code b}, provided it does not overflow.
         *
         * @throws ArithmeticException if {@code a * b} overflows in signed {@code long} arithmetic
         */
        public static long checkedMultiply(long a, long b) {
            // Hacker's Delight, Section 2-12
            int leadingZeros =
                    Long.numberOfLeadingZeros(a)
                            + Long.numberOfLeadingZeros(~a)
                            + Long.numberOfLeadingZeros(b)
                            + Long.numberOfLeadingZeros(~b);
    /*
     * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's < Long.SIZE it's definitely
     * bad. We do the leadingZeros check to avoid the division below if at all possible.
     *
     * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a are 0 and 1. We take
     * care of all a < 0 with their own check, because in particular, the case a == -1 will
     * incorrectly pass the division check below.
     *
     * In all other cases, we check that either a is 0 or the result is consistent with division.
     */
            if (leadingZeros > Long.SIZE + 1) {
                return a * b;
            }
            checkNoOverflow(leadingZeros >= Long.SIZE);
            checkNoOverflow(a >= 0 | b != Long.MIN_VALUE);
            long result = a * b;
            checkNoOverflow(a == 0 || result / a == b);
            return result;
        }
    }

    /*
     * Copyright (C) 2008 The Guava Authors
     *
     * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
     * in compliance with the License. You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software distributed under the License
     * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
     * or implied. See the License for the specific language governing permissions and limitations under
     * the License.
     */
    public static class Ints {
        private Ints() {

        }

        /**
         * Returns the {@code int} nearest in value to {@code value}.
         *
         * @param value any {@code long} value
         * @return the same value cast to {@code int} if it is in the range of the {@code int} type,
         *     {@link Integer#MAX_VALUE} if it is too large, or {@link Integer#MIN_VALUE} if it is too
         *     small
         */
        public static int saturatedCast(long value) {
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (value < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) value;
        }

    }

    public static class Preconditions {

        public static void checkArgument(boolean valid, String template, Object arg) {
            if (!valid) {
                String message = null;
                try {
                    message = String.format(template, arg);
                } catch (RuntimeException ignore) {
                }
                throw new IllegalArgumentException(message);
            }
        }

        public static void checkState(boolean satisfied, String message) {
            if (!satisfied) {
                throw new IllegalStateException(message);
            }
        }
    }
}
