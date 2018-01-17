package kornell.core.to;

import java.util.List;

public interface LibraryFilesTO {
    public static final String TYPE = TOFactory.PREFIX + "libraryfiles+json";

    List<LibraryFileTO> getLibraryFiles();
    void setLibraryFiles(List<LibraryFileTO> libraryFiles);

}
