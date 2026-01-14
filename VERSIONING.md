# Semantic Versioning Strategy

This project follows [Semantic Versioning 2.0.0](https://semver.org/).

## Version Format

```
MAJOR.MINOR.PATCH[-SNAPSHOT]
```

- **MAJOR**: Incompatible API changes
- **MINOR**: Backward-compatible functionality additions
- **PATCH**: Backward-compatible bug fixes
- **SNAPSHOT**: Development version (not released)

## Current Version

Current version: **1.0.0-SNAPSHOT**

## Release Process

### 1. Prepare Release

```bash
# Update version and create release tag
mvn release:prepare

# This will:
# - Remove -SNAPSHOT from version
# - Create Git tag (v1.0.0)
# - Increment version to next SNAPSHOT
```

### 2. Perform Release

```bash
# Build and deploy release
mvn release:perform
```

### 3. Manual Version Update

```bash
# Set specific version
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT

# Commit the change
mvn versions:commit
```

## Version Increment Rules

### MAJOR Version (X.0.0)

Increment when making **incompatible API changes**:
- Removing endpoints
- Changing request/response structure
- Removing configuration properties
- Breaking changes in event schema

**Example**: `1.5.3` → `2.0.0`

```bash
mvn versions:set -DnewVersion=2.0.0-SNAPSHOT
```

### MINOR Version (x.Y.0)

Increment when adding **backward-compatible functionality**:
- New API endpoints
- New configuration options
- New event types
- New features

**Example**: `1.5.3` → `1.6.0`

```bash
mvn versions:set -DnewVersion=1.6.0-SNAPSHOT
```

### PATCH Version (x.y.Z)

Increment for **backward-compatible bug fixes**:
- Bug fixes
- Performance improvements
- Documentation updates
- Security patches

**Example**: `1.5.3` → `1.5.4`

```bash
mvn versions:set -DnewVersion=1.5.4-SNAPSHOT
```

## Git Tagging Convention

Tags follow the format: `v{version}`

Examples:
- `v1.0.0` - First major release
- `v1.1.0` - Minor feature release
- `v1.1.1` - Patch release

## Version Information Endpoints

### Build Info Endpoint

```bash
curl http://localhost:8081/actuator/info
```

**Response:**
```json
{
  "build": {
    "version": "1.0.0",
    "artifact": "kafka-producer",
    "name": "kafka-producer",
    "time": "2024-01-14T12:00:00.000Z",
    "group": "org.charter.mds.elf"
  },
  "git": {
    "branch": "main",
    "commit": {
      "id": "abc123def456",
      "time": "2024-01-14T11:30:00Z"
    }
  }
}
```

### Swagger API Version

Version is displayed in Swagger UI:
```
http://localhost:8081/swagger-ui.html
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Build
        run: mvn clean package
      - name: Create Release
        uses: actions/create-release@v1
        with:
          tag_name: ${{ github.ref }}
          release_name: Release ${{ github.ref }}
```

### GitLab CI Example

```yaml
release:
  stage: deploy
  only:
    - tags
  script:
    - mvn clean package
    - mvn release:perform
```

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | TBD | Initial production release |
| 0.1.0 | TBD | Beta release |

## Best Practices

1. **Always use SNAPSHOT for development**
   ```xml
   <version>1.0.0-SNAPSHOT</version>
   ```

2. **Tag releases in Git**
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

3. **Update CHANGELOG.md** with each release

4. **Document breaking changes** in release notes

5. **Test thoroughly** before incrementing MAJOR version

## Rollback Strategy

If a release has issues:

```bash
# Revert to previous version
git revert <commit-hash>

# Create hotfix version
mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
```

## References

- [Semantic Versioning 2.0.0](https://semver.org/)
- [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
- [Git Commit ID Plugin](https://github.com/git-commit-id/git-commit-id-maven-plugin)
