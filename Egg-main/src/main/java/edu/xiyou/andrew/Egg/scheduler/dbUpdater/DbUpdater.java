package edu.xiyou.andrew.Egg.scheduler.dbUpdater;

/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import edu.xiyou.andrew.Egg.net.CrawlDatum;

import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 15-6-6.
 */
public interface DbUpdater {
    final static String VISITED_DB = "visitedDB";
    final static String DATUMS_DB = "datumsDB";
    final static String LINKS_DB = "linksDB";

//    public Map<String, Long> readFromVisited();

//    public List<String> readFromLinks();

    public List<String> readFromDatums();

    public void write2Visited(Map<String, String> map);

    public void write2Visited(List<CrawlDatum> datums);

    public void write2Links(List<String> urls);

    public void write2Datums(List<String> urls);

    public void merge();
}
