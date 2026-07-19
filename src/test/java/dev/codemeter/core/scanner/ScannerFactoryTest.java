package dev.codemeter.core.scanner;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ScannerFactoryTest {

    @Test
    void availableScannerName_returnsNonNullString() {
        String name = ScannerFactory.availableScannerName();
        assertThat(name).isNotNull();
        assertThat(name).isIn("scc", "cloc", "none");
    }

    @Test
    void tryCreate_returnsOptional() {
        var scanner = ScannerFactory.tryCreate();
        assertThat(scanner).isNotNull();
        // Will be present if scc/cloc is installed, empty otherwise
    }

    @Test
    void sccScanner_returnsCorrectName() {
        SccScanner scc = new SccScanner();
        assertThat(scc.name()).isEqualTo("scc");
    }

    @Test
    void clocScanner_returnsCorrectName() {
        ClocScanner cloc = new ClocScanner();
        assertThat(cloc.name()).isEqualTo("cloc");
    }
}
