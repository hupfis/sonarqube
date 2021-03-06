/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.issue;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.internal.DefaultIssue;
import org.sonar.api.issue.internal.IssueChangeContext;
import org.sonar.core.issue.IssueUpdater;
import org.sonar.server.user.MockUserSession;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CommentActionTest {

  private CommentAction action;

  private IssueUpdater issueUpdater = mock(IssueUpdater.class);

  @Before
  public void before(){
    action = new CommentAction(issueUpdater);
  }

  @Test
  public void should_execute(){
    String comment = "My bulk change comment";
    Map<String, Object> properties = newHashMap();
    properties.put("comment", comment);
    DefaultIssue issue = mock(DefaultIssue.class);

    Action.Context context = mock(Action.Context.class);
    when(context.issue()).thenReturn(issue);

    action.execute(properties, context);
    verify(issueUpdater).addComment(eq(issue), eq(comment), any(IssueChangeContext.class));
  }

  @Test
  public void should_verify_fail_if_parameter_not_found() {
    Map<String, Object> properties = newHashMap();
    properties.put("unknwown", "unknown value");
    try {
      action.verify(properties, Lists.<Issue>newArrayList(), MockUserSession.create());
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("Missing parameter : 'comment'");
    }
    verifyZeroInteractions(issueUpdater);
  }

  @Test
  public void should_support_all_issues(){
    assertThat(action.supports(new DefaultIssue().setResolution(null))).isTrue();
    assertThat(action.supports(new DefaultIssue().setResolution(Issue.RESOLUTION_FIXED))).isTrue();
  }

}
