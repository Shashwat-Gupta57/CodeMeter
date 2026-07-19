package dev.codemeter.tui;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RendererTest {

    @Test
    void truncate_shortString_noChange() {
        assertThat(Renderer.truncate("hello", 10)).isEqualTo("hello");
    }

    @Test
    void truncate_longString_addEllipsis() {
        assertThat(Renderer.truncate("hello world", 8)).isEqualTo("hello...");
    }

    @Test
    void truncate_exactLength_noChange() {
        assertThat(Renderer.truncate("hello", 5)).isEqualTo("hello");
    }

    @Test
    void truncate_nullInput_returnsEmpty() {
        assertThat(Renderer.truncate(null, 10)).isEqualTo("");
    }

    @Test
    void fit_paddsShortStrings() {
        String result = Renderer.fit("hi", 5);
        assertThat(result).isEqualTo("hi   ");
        assertThat(result).hasSize(5);
    }

    @Test
    void fit_truncatesLongStrings() {
        String result = Renderer.fit("hello world", 8);
        assertThat(result).hasSize(8);
    }

    @Test
    void rightAlign_padsFromLeft() {
        String result = Renderer.rightAlign("42", 6);
        assertThat(result).isEqualTo("    42");
        assertThat(result).hasSize(6);
    }

    @Test
    void rightAlign_truncatesIfTooLong() {
        String result = Renderer.rightAlign("hello world", 5);
        assertThat(result).hasSize(5);
    }
}
