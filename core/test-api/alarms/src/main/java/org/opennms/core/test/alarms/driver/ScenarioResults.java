/*
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.opennms.core.test.alarms.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opennms.netmgt.model.OnmsAlarm;

import com.google.common.collect.Lists;

public class ScenarioResults {

    private final Map<Long, List<OnmsAlarm>> alarmsByTime = new LinkedHashMap<>();

    public List<OnmsAlarm> getAlarms(long time) {
        return alarmsByTime.getOrDefault(time, Collections.emptyList());
    }

    public OnmsAlarm getAlarmAt(long time, int id) {
        return getAlarms(time).stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alarm " + id + " not found at time: " + time));
    }

    public List<OnmsAlarm> getAlarmsAtLastKnownTime() {
        final Long lastTime = getLastKnownTime();
        return alarmsByTime.get(lastTime);
    }

    public List<OnmsAlarm> getAcknowledgedAlarms(long time) {
        return getAlarms(time).stream()
                .filter(alarm -> alarm.getAckTime() != null && alarm.getAckUser() != null)
                .collect(Collectors.toList());
    }

    public List<OnmsAlarm> getUnAcknowledgedAlarms(long time) {
        return getAlarms(time).stream()
                .filter(alarm -> alarm.getAckTime() == null && alarm.getAckUser() == null)
                .collect(Collectors.toList());
    }

    public Long getLastKnownTime() {
        return alarmsByTime.keySet().stream()
                .max(Comparator.comparingLong(l -> l ))
                .orElseThrow(() -> new IllegalArgumentException("No times are known."));
    }

    public List<OnmsAlarm> getSituations(long time) {
        return getAlarms(time).stream()
                .filter(OnmsAlarm::isSituation)
                .collect(Collectors.toList());
    }

    public OnmsAlarm getSituation(long time) {
        return getSituations(time).stream()
                .findFirst()
                .orElse(null);
    }

    public OnmsAlarm getProblemAlarm(long time) {
        return getFirstAlarmWithType(time, OnmsAlarm.PROBLEM_TYPE, "problem");
    }

    public OnmsAlarm getResolutionAlarm(long time) {
        return getFirstAlarmWithType(time, OnmsAlarm.RESOLUTION_TYPE, "resolution");
    }

    public OnmsAlarm getNotificationAlarm(long time) {
        return getFirstAlarmWithType(time, OnmsAlarm.PROBLEM_WITHOUT_RESOLUTION_TYPE, "notification");
    }

    private OnmsAlarm getFirstAlarmWithType(long time, int type, String typeDescr) {
        return getAlarms(time).stream()
                .filter(a -> a.getAlarmType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No " + typeDescr + " alarms at time: " + time));
    }

    public List<State> getStateChangesForAlarmWithId(Integer id) {
        // Build a sorted list of all known alarm states
        final List<State> states = alarmsByTime.entrySet().stream()
                .map(e -> {
                    Optional<OnmsAlarm> alarm = e.getValue().stream()
                            .filter(a -> Objects.equals(id, a.getId()))
                            .findFirst();
                    return alarm.map(onmsAlarm -> new State(e.getKey(), onmsAlarm));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(State::getTime))
                .collect(Collectors.toList());

        if (states.size() < 1) {
            throw new IllegalStateException("No known state for alarm with id: " + id);
        }

        final List<State> stateChanges = new ArrayList<>();
        State lastState = null;
        for (State state : states) {
            if (lastState == null) {
                // Initial state
                stateChanges.add(state);
                lastState = state;
                continue;
            }

            if (!areAlarmsEqual(state.getAlarm(), lastState.getAlarm())) {
                // state has changed
                stateChanges.add(state);
                lastState = state;
            }
        }

        final long lastSeenAt = states.get(states.size() - 1).getTime();
        final Optional<Long> wasMissingAtLaterTime = alarmsByTime.keySet().stream()
                .filter(t -> t > lastSeenAt)
                .min(Comparator.comparing(t -> t));
        wasMissingAtLaterTime.ifPresent(aLong -> stateChanges.add(new State(aLong, null)));
        return stateChanges;
    }

    public boolean areAlarmsEqual(OnmsAlarm a1, OnmsAlarm a2) {
        if (a1 == null && a2 == null) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        return Objects.equals(a1.getId(), a2.getId())
            && Objects.equals(a1.getLastEventTime(), a2.getLastEventTime())
                && Objects.equals(a1.getLastAutomationTime(), a2.getLastAutomationTime())
                && Objects.equals(a1.getSeverity(), a2.getSeverity())
                && Objects.equals(a1.getAckUser(), a2.getAckUser())
                && Objects.equals(a1.getAckTime(), a2.getAckTime());
    }

    public void addAlarm(long time, OnmsAlarm alarm) {
        alarmsByTime.compute(time, (k, v) -> {
            if (v == null) {
                return Lists.newArrayList(alarm);
            }
            v.add(alarm);
            return v;
        });
    }

    public void addAlarms(long time, List<OnmsAlarm> alarms) {
        alarmsByTime.compute(time, (k, v) -> {
            if (v == null) {
                return Lists.newArrayList(alarms);
            }
            v.addAll(alarms);
            return v;
        });
    }
}
