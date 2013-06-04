/*
Android Rivers is an app to read and discover news using RiverJs, RSS and OPML format.
Copyright (C) 2012 Dody Gunawinata (dodyg@silverkeytech.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package com.silverkeytech.news_engine.outlines

import com.thebuzzmedia.sjxp.XMLParser
import java.io.InputStream
import com.silverkeytech.news_engine.xml.textRule
import com.thebuzzmedia.sjxp.rule.DefaultRule
import com.silverkeytech.news_engine.xml.attributeRule
import com.silverkeytech.news_engine.xml.tagRule

public class OpmlParser{
    public fun parse(input: InputStream, rss: OpmlBuilder) {
        var parser = XMLParser<OpmlBuilder>(headTitle, headDateCreated, headDateModified, headOwnerName, headOwnerEmail,
                outlineTag, firstOutline
        )
        parser.parse(input, "UTF-8", rss)
    }
}

val headTitle = textRule<OpmlBuilder>("/opml/head/title", {(text, opml) ->
    opml.head.setTitle(text)
 })


val headDateCreated = textRule<OpmlBuilder>("/opml/head/dateCreated", {(text, opml) ->
    opml.head.setDateCreated(text)
})

val headDateModified = textRule<OpmlBuilder>("/opml/head/dateModified", {(text, opml) ->
    opml.head.setDateModified(text)
})

val headOwnerName = textRule<OpmlBuilder>("/opml/head/ownerName", {(text, opml) ->
    opml.head.setOwnerName(text)
})

val headOwnerEmail = textRule<OpmlBuilder>("/opml/head/ownerEmail", {(text, opml) ->
    opml.head.setOwnerEmail(text)
})


/*
val itemTag = tagRule<RssBuilder>("/rss/channel/item", {(isStartTag, rss) ->
    if (isStartTag)
        rss.channel.startItem()
    else
        rss.channel.endItem()
})

*/


val outlineTag = tagRule<OpmlBuilder>("/opml/body/outline", {(isStartTag, opml) ->
    if (isStartTag)
        opml.body.startLevel(0)
    else
        opml.body.endLevel(0)
})


//(path: String, action: (attrName: String, attrValue: String, rss: T) -> Unit, vararg attrNames: String?): DefaultRule<T>
fun outlineAttribute<T>(level : Int, vararg attrNames: String?) : ((String, String, T) -> Unit) -> DefaultRule<T>{
    if (level < 0)
        throw IllegalArgumentException()

    var path = "/opml/body"
    for (i in 0..level)
        path += "/outline"

    return { action ->
        attributeRule<T>(path, action, *attrNames)
    }
}

val firstOutline = outlineAttribute<OpmlBuilder>(0, "text")(
        {   attrName, attrValue, opml ->
            when(attrName){
                "text" -> opml.body.setText(attrValue)
                else -> { } //empty
            }
        }
)
