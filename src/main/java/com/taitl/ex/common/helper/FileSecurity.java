package com.taitl.ex.common.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;

import static com.taitl.ex.common.helper.Args.sane;
import static com.taitl.ex.common.helper.State.verify;

public class FileSecurity
{
    public static void verifySecurePosixFile(Path file, String fileLabel, String troubleshootingSection)
    {
        sane(file, "file", fileLabel, "fileLabel", troubleshootingSection, "troubleshootingSection");

        PosixFileAttributeView view =
                Files.getFileAttributeView(file, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        if (view == null)
        {
            return;
        }
        try
        {
            Set<PosixFilePermission> permissions = view.readAttributes().permissions();
            boolean groupWritable = permissions.contains(PosixFilePermission.GROUP_WRITE);
            boolean otherWritable = permissions.contains(PosixFilePermission.OTHERS_WRITE);
            verify(!groupWritable && !otherWritable,
                    String.format("%s is group/world writable: %s. See %s",
                            fileLabel, file, troubleshootingSection));
            verifyOwnedByCurrentUser(file, fileLabel, troubleshootingSection);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read %s permissions '%s'. See %s",
                            fileLabel, file, troubleshootingSection),
                    e);
        }
    }

    public static void verifyOwnedByCurrentUser(Path file, String fileLabel, String troubleshootingSection)
    {
        sane(file, "file", fileLabel, "fileLabel", troubleshootingSection, "troubleshootingSection");

        String user = System.getProperty("user.name");
        if (user == null || user.isBlank())
        {
            return;
        }
        try
        {
            UserPrincipal owner = Files.getOwner(file, LinkOption.NOFOLLOW_LINKS);
            UserPrincipalLookupService lookup = file.getFileSystem().getUserPrincipalLookupService();
            UserPrincipal current = lookup.lookupPrincipalByName(user);
            verify(owner.equals(current),
                    String.format("%s is owned by a different user: %s. See %s",
                            fileLabel, file, troubleshootingSection));
        }
        catch (IOException | UnsupportedOperationException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read %s ownership '%s'. See %s",
                            fileLabel, file, troubleshootingSection),
                    e);
        }
    }
}
