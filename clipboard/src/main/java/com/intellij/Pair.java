// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Generic wrapper around two related values.
 */
public class Pair<A, B> {
  public final A first;
  public final B second;

  @NotNull
  public static <A, B> Pair<A, B> create(A first, B second) {
    //noinspection DontUsePairConstructor
    return new Pair<A, B>(first, second);
  }

  @NotNull
  public static <A, B> NonNull<A, B> createNonNull(@NotNull A first, @NotNull B second) {
    return new NonNull<A, B>(first, second);
  }

  @NotNull
  public static <A, B> Pair<A, B> pair(A first, B second) {
    //noinspection DontUsePairConstructor
    return new Pair<A, B>(first, second);
  }

  public static <T> T getFirst(@Nullable Pair<T, ?> pair) {
    return pair != null ? pair.first : null;
  }

  public static <T> T getSecond(@Nullable Pair<?, T> pair) {
    return pair != null ? pair.second : null;
  }

  @SuppressWarnings("rawtypes")
  private static final Pair EMPTY = create(null, null);

  @NotNull
  public static <A, B> Pair<A, B> empty() {
    //noinspection unchecked
    return EMPTY;
  }

  /**
   * @see #create(Object, Object)
   */
  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public final A getFirst() {
    return first;
  }

  public final B getSecond() {
    return second;
  }

  @Override
  public final boolean equals(Object o) {
    return o instanceof Pair && Comparing.equal(first, ((Pair<?, ?>) o).first) && Comparing.equal(second, ((Pair<?, ?>) o).second);
  }

  @Override
  public int hashCode() {
    int result = first != null ? first.hashCode() : 0;
    result = 31 * result + (second != null ? second.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "<" + first + "," + second + ">";
  }

  public static class NonNull<A, B> extends Pair</*@NotNull*/ A, /*@NotNull*/ B> {
    public NonNull(@NotNull A first, @NotNull B second) {
      super(first, second);
    }
  }

  /**
   * @param <A> first value type (Comparable)
   * @param <B> second value type
   * @return comparator that compares pair values by first value
   */
  public static <A extends Comparable<? super A>, B> Comparator<Pair<A, B>> comparingByFirst() {
    return new Comparator<Pair<A, B>>() {
      @Override
      public int compare(Pair<A, B> o1, Pair<A, B> o2) {
        return o1.first.compareTo(o2.first);
      }
    };
  }

  /**
   * @param <A> first value type
   * @param <B> second value type (Comparable)
   * @return comparator that compares pair values by second value
   */
  public static <A, B extends Comparable<? super B>> Comparator<Pair<A, B>> comparingBySecond() {
    return new Comparator<Pair<A, B>>() {
      @Override
      public int compare(Pair<A, B> o1, Pair<A, B> o2) {
        return o1.second.compareTo(o2.second);
      }
    };
  }
}
