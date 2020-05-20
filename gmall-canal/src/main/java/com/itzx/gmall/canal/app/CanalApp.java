package com.itzx.gmall.canal.app;

import com.itzx.gmall.canal.client.CanalClient;

/**
 * author: yyeleven
 * create: 2020/5/13 00:36
 */
public class CanalApp {

    public static void main(String[] args) {

        final CanalClient canalClient = new CanalClient();

        canalClient.watch("node-1", 11111, "example", "gmall.*");




    }
}
