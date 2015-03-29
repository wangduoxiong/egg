package edu.xiyou.andrew.Egg.schedule;

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

import edu.xiyou.andrew.Egg.parse.CrawlDatum;
import edu.xiyou.andrew.Egg.schedule.persistent.DbUpdater;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-3-28.
 */
public class QueueSchedule extends AbtractQueueSchedule{

    public QueueSchedule(DbUpdater updater){
        this.updater = updater;
        List<String> datums = updater.readFromDatums();
        queue = new ArrayBlockingQueue<String>(datums.size(), false, datums);
    }
}
