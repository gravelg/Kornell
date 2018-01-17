package kornell.core.entity;

public enum CertificateType {
    DEFAULT ("reports/certificate.jasper"),
    NO_BG ("reports/certificate-noBg.jasper");

    private final String path;

    CertificateType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
