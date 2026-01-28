package io.cryptoarena.common;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Allure-annotated test for CryptoArenaCommon.
 *
 * Run tests: ./gradlew test
 * Generate report: allure serve common/allure-results
 */
@Epic("Crypto Arena Platform")
@Feature("Common Module")
class CryptoArenaAllureTest {

    @Test
    @Story("Version Management")
    @DisplayName("Should have correct version")
    @Description("Verifies that the crypto arena module reports the correct version")
    @Severity(SeverityLevel.CRITICAL)
    void shouldHaveCorrectVersion() {
        String version = getModuleVersion();
        verifyVersion(version, "1.0-SNAPSHOT");
    }

    @Test
    @Story("Version Management")
    @DisplayName("Version should follow semantic versioning")
    @Description("Verifies that the version follows semantic versioning pattern")
    @Severity(SeverityLevel.MINOR)
    void versionShouldFollowSemanticVersioning() {
        String version = getModuleVersion();
        verifySemverPattern(version);
    }

    @Test
    @Story("Version Management")
    @DisplayName("Version should not be empty")
    @Description("Verifies that the version is not empty")
    @Severity(SeverityLevel.NORMAL)
    void versionShouldNotBeEmpty() {
        String version = getModuleVersion();
        verifyVersionNotEmpty(version);
    }

    @Test
    @Story("Module Initialization")
    @DisplayName("Should have valid module name")
    @Description("Verifies that the module name is properly defined")
    @Severity(SeverityLevel.NORMAL)
    void shouldHaveValidModuleName() {
        String moduleName = getModuleName();
        verifyModuleName(moduleName);
    }

    @Step("Get module version")
    private String getModuleVersion() {
        return CryptoArenaCommon.VERSION;
    }

    @Step("Get module name")
    private String getModuleName() {
        return CryptoArenaCommon.MODULE_NAME;
    }

    @Step("Verify version is {expectedVersion}")
    private void verifyVersion(String version, String expectedVersion) {
        assertThat(version).isEqualTo(expectedVersion);
    }

    @Step("Verify version matches semver pattern")
    private void verifySemverPattern(String version) {
        assertThat(version).matches("\\d+\\.\\d+.*");
    }

    @Step("Verify version is not empty")
    private void verifyVersionNotEmpty(String version) {
        assertThat(version).isNotEmpty();
    }

    @Step("Verify module name is valid")
    private void verifyModuleName(String moduleName) {
        assertThat(moduleName)
            .isNotEmpty()
            .contains("common");
    }
}
