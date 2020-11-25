package android.content.pm;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.ArraySet;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Set;

public class PackageParser {
    public Package parseMonolithicPackage(File apkFile, int flags) {
        throw new RuntimeException("Stub");
    }

    public static PackageInfo generatePackageInfo(Package p,
                                                  int gids[], int flags, long firstInstallTime, long lastUpdateTime,
                                                  Set<String> grantedPermissions, PackageUserState state) {
        throw new RuntimeException("Stub");
    }

    public static void collectCertificates(Package pkg, boolean skipVerify) {
        throw new RuntimeException("Stub");
    }

    public final static class Package {
        public String packageName;
        public String manifestPackageName;

        public String[] splitNames;
        public String volumeUuid;

        public String codePath;
        public String baseCodePath;
        public String[] splitCodePaths;
        public int baseRevisionCode;
        public int[] splitRevisionCodes;

        public int[] splitFlags;
        public int[] splitPrivateFlags;

        public boolean baseHardwareAccelerated;

        public ApplicationInfo applicationInfo = new ApplicationInfo();

        public final ArrayList<Permission> permissions = new ArrayList<Permission>(0);
        public final ArrayList<PermissionGroup> permissionGroups = new ArrayList<PermissionGroup>(0);
        public final ArrayList<Activity> activities = new ArrayList<Activity>(0);
        public final ArrayList<Activity> receivers = new ArrayList<Activity>(0);
        public final ArrayList<Provider> providers = new ArrayList<Provider>(0);
        public final ArrayList<Service> services = new ArrayList<Service>(0);
        public final ArrayList<Instrumentation> instrumentation = new ArrayList<Instrumentation>(0);

        public final ArrayList<String> requestedPermissions = new ArrayList<String>();

        public ArrayList<String> protectedBroadcasts;

        public Package parentPackage;
        public ArrayList<Package> childPackages;

        public String staticSharedLibName = null;
        public long staticSharedLibVersion = 0;
        public ArrayList<String> libraryNames = null;
        public ArrayList<String> usesLibraries = null;
        public ArrayList<String> usesStaticLibraries = null;
        public long[] usesStaticLibrariesVersions = null;
        public String[][] usesStaticLibrariesCertDigests = null;
        public ArrayList<String> usesOptionalLibraries = null;
        public String[] usesLibraryFiles = null;
        public ArrayList<ActivityIntentInfo> preferredActivityFilters = null;
        public ArrayList<String> mOriginalPackages = null;
        public String mRealPackage = null;
        public ArrayList<String> mAdoptPermissions = null;

        public Bundle mAppMetaData = null;

        public int mVersionCode;
        public int mVersionCodeMajor;
        public String mVersionName;

        public String mSharedUserId;

        public int mSharedUserLabel;

        //        public SigningDetails mSigningDetails = SigningDetails.UNKNOWN;
        public int mPreferredOrder = 0;
        public Object mExtras;
        public ArrayList<ConfigurationInfo> configPreferences = null;
        public ArrayList<FeatureInfo> reqFeatures = null;
        public ArrayList<FeatureGroupInfo> featureGroups = null;

        public int installLocation;

        public boolean coreApp;

        public boolean mRequiredForAllUsers;

        public String mRestrictedAccountType;
        public String mRequiredAccountType;
        public String mOverlayTarget;
        public String mOverlayCategory;
        public int mOverlayPriority;
        public boolean mOverlayIsStatic;

        public int mCompileSdkVersion;
        public String mCompileSdkVersionCodename;

        public ArraySet<String> mUpgradeKeySets;
        public ArrayMap<String, ArraySet<PublicKey>> mKeySetMapping;

        public String cpuAbiOverride;
        public boolean use32bitAbi;

        public byte[] restrictUpdateHash;

        public boolean visibleToInstantApps;
        public boolean isStub;
    }

    public final static class Permission extends Component<IntentInfo> {

    }

    public static abstract class IntentInfo extends IntentFilter {

    }

    public static abstract class Component<II extends IntentInfo> {
        public ArrayList<II> intents;
    }

    public final static class PermissionGroup extends Component<IntentInfo> {

    }

    public final static class Activity extends Component<ActivityIntentInfo> {
        public ActivityInfo info;
    }

    public final static class ActivityIntentInfo extends IntentInfo {
    }

    public static final class ProviderIntentInfo extends IntentInfo {

    }

    public final static class Provider extends Component<ProviderIntentInfo> {

    }

    public final static class Service extends Component<ServiceIntentInfo> {

    }

    public final static class ServiceIntentInfo extends IntentInfo {

    }

    public final static class Instrumentation extends Component<IntentInfo> {

    }
}
