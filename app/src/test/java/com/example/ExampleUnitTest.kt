package com.example

import com.example.data.model.BlacklistedUserEntity
import com.example.data.model.ModerationReportEntity
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun blacklistedUserEntity_creationAndStatus() {
    val user = BlacklistedUserEntity(
      userHandle = "@abusive_user",
      userName = "Abusive Troll",
      reason = "गाली-गलौज और अभद्र टिप्पणी",
      status = "BANNED"
    )
    assertEquals("@abusive_user", user.userHandle)
    assertEquals("BANNED", user.status)
    assertTrue(user.reason.contains("गाली-गलौज"))
  }

  @Test
  fun moderationReportEntity_creation() {
    val report = ModerationReportEntity(
      targetType = "COMMENT",
      targetId = 101L,
      reportedHandle = "@violator",
      reportedName = "Rule Violator",
      contentSnippet = "Bad text snippet",
      violationType = "ABUSIVE_LANGUAGE",
      status = "PENDING"
    )
    assertEquals("COMMENT", report.targetType)
    assertEquals("PENDING", report.status)
    assertEquals("ABUSIVE_LANGUAGE", report.violationType)
  }
}
