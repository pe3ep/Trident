package cc.pe3epwithyou.trident.modrinth

import kotlinx.serialization.Serializable

object VersionResponseSchema {
    @Serializable
    data class ModrinthVersion(
        val game_versions: List<String>,
        val loaders: List<String>,
        val id: String,
        val project_id: String,
        val author_id: String,
        val featured: Boolean,
        val name: String,
        val version_number: String,
        val changelog: String,
        val changelog_url: String?,
        val date_published: String,
        val downloads: Int,
        val version_type: String,
        val status: String,
        val requested_status: String?,
        val files: List<ModrinthFile>,
        val dependencies: List<ModrinthDependency>
    )

    @Serializable
    data class ModrinthFile(
        val hashes: ModrinthHashes,
        val url: String,
        val filename: String,
        val primary: Boolean,
        val size: Int,
        val file_type: String?
    )

    @Serializable
    data class ModrinthHashes(
        val sha512: String, val sha1: String
    )

    @Serializable
    data class ModrinthDependency(
        val version_id: String?, val project_id: String?, val file_name: String?, val dependency_type: String
    )
}