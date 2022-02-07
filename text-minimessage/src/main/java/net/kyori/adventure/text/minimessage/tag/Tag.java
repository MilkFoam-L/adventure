/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2022 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.text.minimessage.tag;

import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * A tag definition for the MiniMessage language.
 *
 * <p>All implementations of {@code Tag} must implement one of {@link Inserting}, {@link Modifying}, or {@link PreProcess}.</p>
 *
 * @since 4.10.0
 */
public /* sealed */ interface Tag /* permits Inserting, Modifying, PreProcess */ {

  /**
   * Create a tag that inserts the content literally into the parse string.
   *
   * @param content content to insert
   * @return a new tag
   * @since 4.10.0
   */
  static @NotNull PreProcess miniMessage(final @NotNull String content) {
    return () -> content;
  }

  /**
   * Create a tag that will insert a certain component into the output.
   *
   * <p>Any content between this tag and an explicit closing tag will become a child of {@code content}.</p>
   *
   * @param content the content to insert.
   * @return a tag that will insert this component
   * @since 4.10.0
   */
  static @NotNull Tag inserting(final @NotNull Component content) {
    requireNonNull(content, "content must not be null");
    return (Inserting) () -> content;
  }

  /**
   * Create a tag that will insert a certain component into the output.
   *
   * <p>Any content between this tag and an explicit closing tag will become a child of {@code content}.</p>
   *
   * @param value the content to insert, eagerly converted to a component
   * @return a tag that will insert this component
   * @since 4.10.0
   */
  static @NotNull Tag inserting(final @NotNull ComponentLike value) {
    return inserting(requireNonNull(value, "value").asComponent());
  }

  /**
   * Create a tag that will apply a certain style to components.
   *
   * @param styles the action applied to the component style
   * @return a tag for this action
   * @since 4.10.0
   */
  static @NotNull Tag styling(final Consumer<Style.Builder> styles) {
    return new Inserting() {
      @Override
      public Component value() {
        return Component.text("", Style.style(styles));
      }
    };
  }

  /**
   * Create a tag that will apply certain styles to components.
   *
   * @param actions the style builder actions
   * @return a tag for these actions
   * @since 4.10.0
   */
  static @NotNull Tag styling(final @NotNull StyleBuilderApplicable@NotNull... actions) {
    requireNonNull(actions, "actions");
    for (int i = 0, length = actions.length; i < length; i++) {
      if (actions[i] == null) {
        throw new NullPointerException("actions[" + i + "]");
      }
    }

    return styling(builder -> {
      for (final StyleBuilderApplicable action : actions) {
        action.styleApply(builder);
      }
    });
  }

  /**
   * Get whether or not this tag allows children.
   *
   * <p>If children are not allowed, this tag will be auto-closing, and should not be closed explicitly.</p>
   *
   * @return whether this tag will allow following to become children
   * @since 4.10.0
   */
  default boolean allowsChildren() {
    return true;
  }

  /**
   * An argument that can be passed to a tag, after the first {@code :}.
   *
   * @since 4.10.0
   */
  @ApiStatus.NonExtendable
  interface Argument {
    /**
     * Returns the value of this tag part.
     *
     * @return the value
     * @since 4.10.0
     */
    @NotNull String value();

    /**
     * Checks if this tag part represents {@code true}.
     *
     * @return if this tag part represents {@code true}
     * @since 4.10.0
     */
    default boolean isTrue() {
      return "true".equals(this.value()) || "on".equals(this.value());
    }

    /**
     * Checks if this tag part represents {@code false}.
     *
     * @return if this tag part represents {@code false}
     * @since 4.10.0
     */
    default boolean isFalse() {
      return "false".equals(this.value()) || "off".equals(this.value());
    }
  }
}
