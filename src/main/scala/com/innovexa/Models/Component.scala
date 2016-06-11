package com.innovexa.Models

case class Component(jcrPath: String,
                    resourceSuperType: Option[String],
                    title: Option[String],
                    componentGroup: Option[String])
